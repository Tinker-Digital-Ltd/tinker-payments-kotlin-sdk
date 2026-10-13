package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.exception.ApiException
import co.ke.tinker.http.HttpClient
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
    fun `retains legacy exceptions`() {
        val error = ApiException("legacy", 123)
        assertEquals(123, error.code)
        assertEquals("legacy", error.message)
    }
}
