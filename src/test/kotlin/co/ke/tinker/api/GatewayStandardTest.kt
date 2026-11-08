package co.ke.tinker.api

import co.ke.tinker.Payments
import co.ke.tinker.enums.PaymentStatus
import co.ke.tinker.exception.ApiException
import co.ke.tinker.exception.FeatureNotSupportedException
import co.ke.tinker.model.dto.InitiatePaymentRequestDto
import co.ke.tinker.model.dto.QueryPaymentRequestDto
import com.fasterxml.jackson.databind.JsonNode
import com.sun.net.httpserver.HttpServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress

class GatewayStandardTest {
    private val mapper = LifecycleCodec.mapper

    @Test
    fun `gateway cases follow the shared fixture`() {
        val rows = mapper.readTree(javaClass.getResourceAsStream("/merchant-lifecycle.json"))["gateway"]["cases"]
        var row = rows[0]
        var path = ""
        var body = ""
        var key: String? = null
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            val response = if (exchange.requestURI.path.endsWith("/auth/token")) {
                """{"token":"token","expires_in":3600}"""
            } else {
                path = exchange.requestURI.toString().substring(4)
                body = exchange.requestBody.readBytes().toString(Charsets.UTF_8)
                key = exchange.requestHeaders.getFirst("Idempotency-Key")
                row["response"].toString()
            }
            val status = if (exchange.requestURI.path.endsWith("/auth/token")) 200 else row["status"].asInt()
            val bytes = response.toByteArray(Charsets.UTF_8)
            exchange.sendResponseHeaders(status, bytes.size.toLong())
            exchange.responseBody.write(bytes)
            exchange.close()
        }
        server.start()
        try {
            val client = Payments("public", "secret", baseUrl = "http://127.0.0.1:${server.address.port}")
            val tx = client.transactions()

            row = rows[0]
            val caps = tx.capabilities("KES", "KE")
            assertEquals("merchant/payment/capabilities?currency=KES&country=KE", path)
            assertEquals(listOf("stripe", "mpesa", "futurepay"), caps.providers.map { it.gateway })
            assertEquals("native", caps.providers[2].capabilities["future_feature"])

            row = rows[1]
            val sent = row["body"]
            val error = assertThrows(FeatureNotSupportedException::class.java) {
                tx.initiate(InitiatePaymentRequestDto(100.0, "KES", "mpesa", "ORDER-1", "https://example.com/return", customerPhone = "+254712345678", paymentMethod = "apple_pay"))
            }
            assertTrue(sameJson(sent, mapper.readTree(body)))
            assertEquals(422, error.httpStatus)
            assertEquals("FEATURE_NOT_SUPPORTED", error.errorCode)
            assertEquals("mpesa", error.gateway)
            assertEquals("wallet", error.feature)
            assertEquals(listOf("stripe"), error.alternatives)
            assertEquals(ApiException::class.java, error.javaClass.superclass)
            assertNull(key)

            row = rows[2]
            val started = tx.initiate(InitiatePaymentRequestDto(100.0, "KES", "futurepay", "ORDER-2", "https://example.com/return"))
            assertTrue(sameJson(row["body"], mapper.readTree(body)))
            assertEquals("PAY-future1", started.initiationData?.paymentReference)
            assertEquals(PaymentStatus.PENDING, started.status)

            row = rows[3]
            val queried = tx.query(QueryPaymentRequestDto("PAY-future1", "futurepay"))
            assertTrue(sameJson(row["body"], mapper.readTree(body)))
            assertEquals(PaymentStatus.REVERSED, queried.status)
            assertEquals("reversed", queried.rawStatus)
        } finally {
            server.stop(0)
        }
    }

    private fun sameJson(expected: JsonNode, actual: JsonNode): Boolean = expected.equals(
        { a, b -> if (a.isNumber && b.isNumber) a.asDouble().compareTo(b.asDouble()) else if (a == b) 0 else 1 },
        actual,
    )

    @Test
    fun `unknown status is kept as sent and completed maps to success`() {
        val unknown = co.ke.tinker.model.Transaction(mapOf("status" to "settling"))
        assertEquals(PaymentStatus.UNKNOWN, unknown.status)
        assertEquals("settling", unknown.rawStatus)
        assertEquals(PaymentStatus.SUCCESS, co.ke.tinker.model.Transaction(mapOf("status" to "completed")).status)
    }
}
