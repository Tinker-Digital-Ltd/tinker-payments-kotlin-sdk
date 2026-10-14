package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.exception.ApiException
import co.ke.tinker.http.HttpClient
import co.ke.tinker.model.dto.ManagedCheckout
import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.net.httpserver.HttpServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress

class ManagedBillingTest {
    private lateinit var server: HttpServer
    private lateinit var manager: SubscriptionManager
    private val mapper = ObjectMapper()
    private val calls = mutableListOf<List<Any?>>()
    private var status = 200
    private var responseBody = """{"success":true,"data":{"snapshot":123,"next_cursor":"in_next","has_more":true}}"""

    @BeforeEach
    fun setUp() {
        server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            val body: String
            val responseStatus: Int
            if (exchange.requestURI.path.endsWith("/auth/token")) {
                body = """{"token":"token","expires_in":3600}"""
                responseStatus = 200
            } else {
                val requestBody = exchange.requestBody.readBytes().toString(Charsets.UTF_8)
                calls.add(
                    listOf(
                        exchange.requestMethod,
                        exchange.requestURI.toString().substring(4),
                        if (requestBody.isEmpty()) null else mapper.readValue(requestBody, Map::class.java),
                    ),
                )
                assertEquals("Bearer token", exchange.requestHeaders.getFirst("Authorization"))
                body = responseBody
                responseStatus = status
            }
            exchange.responseHeaders.add("x-request-id", "header-id")
            exchange.responseHeaders.add("rEtRy-AfTeR", "30")
            val bytes = body.toByteArray(Charsets.UTF_8)
            exchange.sendResponseHeaders(responseStatus, bytes.size.toLong())
            exchange.responseBody.write(bytes)
            exchange.close()
        }
        server.start()
        val config = Configuration("public", "secret", "http://127.0.0.1:${server.address.port}")
        val http = HttpClient()
        manager = SubscriptionManager(config, http, AuthenticationManager(config, http))
    }

    @AfterEach
    fun tearDown() {
        if (::server.isInitialized) server.stop(0)
    }

    @Test
    fun `sends all managed operations with exact payloads and escaped IDs`() {
        val checkout = ManagedCheckout("workspace", "buyer@example.com", "Buyer", "EUR", 15000, 3, "year", "https://example.com/return", "checkout-key")
        assertEquals(
            mapOf(
                "external_customer_id" to "workspace", "email" to "buyer@example.com", "name" to "Buyer",
                "currency" to "EUR", "unit_amount" to 15000L, "quantity" to 3, "interval" to "year",
                "return_url" to "https://example.com/return", "idempotency_key" to "checkout-key",
            ),
            checkout.toMap(),
        )
        manager.checkout(checkout)
        manager.billingResource("sub/a b")
        manager.changeQuantity("sub/a b", 4, "change-key")
        manager.scheduleQuantity("sub/a b", 2, "schedule-key")
        manager.clearScheduledQuantity("sub/a b", "clear-key")
        manager.setCancelAtRenewal("sub/a b", false, "resume-key")
        manager.billingOperation("key/a b")
        manager.paymentMethodPortal("sub/a b", "https://example.com/return", "portal-key")
        val page = manager.billingPaymentHistory("sub/a b", "in/a &b", 123)
        manager.billingPaymentHistory("sub/a b")
        val base = "merchant/billing/resources/sub%2Fa%20b"
        val expected = listOf(
            listOf("POST", "merchant/billing/checkout", mapper.readValue(mapper.writeValueAsString(checkout.toMap()), Map::class.java)),
            listOf("GET", base, null),
            listOf("POST", "$base/quantity", mapOf("quantity" to 4, "idempotency_key" to "change-key")),
            listOf("POST", "$base/schedule", mapOf("quantity" to 2, "idempotency_key" to "schedule-key")),
            listOf("POST", "$base/unschedule", mapOf("idempotency_key" to "clear-key")),
            listOf("POST", "$base/cancel", mapOf("cancel" to false, "idempotency_key" to "resume-key")),
            listOf("GET", "merchant/billing/operations/key%2Fa%20b", null),
            listOf("POST", "$base/payment-method", mapOf("return_url" to "https://example.com/return", "idempotency_key" to "portal-key")),
            listOf("GET", "$base/payment-history?starting_after=in%2Fa%20%26b&snapshot=123", null),
            listOf("GET", "$base/payment-history", null),
        )
        assertEquals(expected, calls)
        assertEquals(mapOf("snapshot" to 123, "next_cursor" to "in_next", "has_more" to true), page)
    }

    @Test
    fun `retains recovery metadata for HTTP and envelope failures`() {
        for (responseStatus in listOf(429, 200, 502)) {
            status = responseStatus
            responseBody = if (status == 502) "<html>Bad gateway</html>" else """{"success":false,"error":{"message":"Try later","code":"RATE_LIMITED","provider_code":"rate_limit","request_id":"body-id","outcome":"not_applied"}}"""
            val error = assertThrows(ApiException::class.java) { manager.listPlans() }
            assertEquals(status, error.httpStatus)
            assertEquals("30", error.retryAfter)
            assertEquals(if (status == 502) "header-id" else "body-id", error.requestId)
            assertEquals(if (status == 502) null else "RATE_LIMITED", error.errorCode)
            assertEquals(if (status == 502) null else "rate_limit", error.providerCode)
            assertEquals(if (status == 502) null else "not_applied", error.outcome)
        }
    }

    @Test
    fun `retains legacy exceptions and large integer prices`() {
        val error = ApiException("legacy", 123)
        assertEquals(123, error.code)
        assertEquals("legacy", error.message)
        val checkout = ManagedCheckout("workspace", "email", "name", "EUR", 9007199254740993L, 3, "year", "return", "key")
        assertEquals(9007199254740993L, checkout.toMap()["unit_amount"])
    }
}
