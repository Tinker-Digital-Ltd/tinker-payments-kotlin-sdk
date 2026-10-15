package co.ke.tinker.api

import co.ke.tinker.Payments
import co.ke.tinker.exception.ApiException
import co.ke.tinker.model.lifecycle.CancellationRequest
import co.ke.tinker.model.lifecycle.PayoutFilters
import co.ke.tinker.model.lifecycle.PayoutQuoteRequest
import co.ke.tinker.model.lifecycle.PayoutRequest
import co.ke.tinker.model.lifecycle.PayoutStatus
import co.ke.tinker.model.lifecycle.RefundReason
import co.ke.tinker.model.lifecycle.RefundRequest
import com.fasterxml.jackson.databind.JsonNode
import com.sun.net.httpserver.HttpServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class LifecycleTest {
    private val mapper = LifecycleCodec.mapper
    private lateinit var row: JsonNode
    private var calls = 0
    private var actualMethod = ""
    private var actualPath = ""
    private var actualBody = ""
    private var actualKey: String? = null
    private var actualAuthorization: String? = null

    private fun fixture(): JsonNode {
        val snapshot = mapper.readTree(javaClass.getResourceAsStream("/merchant-lifecycle.json"))
        val canonical = Path.of(System.getenv("TINKER_LIFECYCLE_FIXTURE") ?: "../tinker-payments-openapi/fixtures/merchant-lifecycle.json")
        if (Files.exists(canonical)) assertEquals(snapshot, mapper.readTree(canonical.toFile()), "Shared fixture drifted")
        return snapshot
    }

    @Test
    fun `all operations follow the shared lifecycle contract`() {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            val body: String
            val status: Int
            if (exchange.requestURI.path.endsWith("/auth/token")) {
                body = """{"token":"token","expires_in":3600}"""
                status = 200
            } else {
                calls++
                actualMethod = exchange.requestMethod
                actualPath = exchange.requestURI.toString().substring(4)
                actualBody = exchange.requestBody.readBytes().toString(Charsets.UTF_8)
                actualKey = exchange.requestHeaders.getFirst("Idempotency-Key")
                actualAuthorization = exchange.requestHeaders.getFirst("Authorization")
                body = row["response"].toString()
                status = row["status"].asInt()
            }
            val bytes = body.toByteArray(Charsets.UTF_8)
            exchange.sendResponseHeaders(status, bytes.size.toLong())
            exchange.responseBody.write(bytes)
            exchange.close()
        }
        server.start()
        try {
            val client = Payments("public", "secret", baseUrl = "http://127.0.0.1:${server.address.port}")
            for (scenario in fixture()["cases"]) {
                row = scenario
                calls = 0
                val result = invoke(client, row["operation"].asText())
                assertJson(row["response"]["data"], mapper.valueToTree(result))
                assertEquals(1, calls)
                assertEquals(row["method"].asText(), actualMethod)
                val actual = URI.create(actualPath)
                val expected = URI.create(row["path"].asText())
                assertEquals(expected.rawPath, actual.rawPath)
                assertEquals(query(expected), query(actual))
                assertJson(row["body"], if (actualBody.isEmpty()) mapper.nullNode() else mapper.readTree(actualBody))
                assertEquals("Bearer token", actualAuthorization)
                assertEquals(if (row["headers"].has("Idempotency-Key")) "stable-key" else null, actualKey)
            }
            for (scenario in fixture()["errors"]) {
                row = scenario
                val error = assertThrows(ApiException::class.java) {
                    client.transactions().cancelCheckout("payment", CancellationRequest("stop"), "key")
                }
                assertEquals(row["status"].asInt(), error.httpStatus)
                assertEquals(row.at("/response/error/code").asText(), error.errorCode)
                assertJson(row["response"], mapper.valueToTree(error.details))
            }
        } finally {
            server.stop(0)
        }
    }

    private fun invoke(client: Payments, operation: String): Any {
        val tx = client.transactions()
        val payouts = client.payouts()
        return when (operation) {
            "refund" -> tx.refund("payment/a b", RefundRequest(reason = RefundReason.REQUESTED_BY_CUSTOMER, amount = 100.0), " stable-key ")
            "refunds" -> tx.refunds("payment/a b")
            "get_refund" -> tx.getRefund("payment/a b", "refund/a b")
            "payout_methods" -> payouts.methods()
            "payout_quote" -> payouts.quote(PayoutQuoteRequest("method/a &b", 100.0, "JPY"))
            "request_payout" -> payouts.requestPayout(PayoutRequest("method/a &b", 100.0, "JPY", 2.0), "stable-key")
            "payouts" -> payouts.list(PayoutFilters(2, 10, PayoutStatus.PENDING_APPROVAL, "JPY"))
            "get_payout" -> payouts.get("payout/a b")
            "cancel_checkout" -> tx.cancelCheckout("payment/a b", CancellationRequest("Customer left checkout"), "stable-key")
            "cancellations" -> tx.cancellations("payment/a b")
            "get_cancellation" -> tx.getCancellation("payment/a b", "cancel/a b")
            else -> error("Unhandled fixture operation $operation")
        }
    }

    @Test
    fun `invalid amounts and keys never reach transport`() {
        val client = Payments("public", "secret")
        for (key in listOf("", " ", "é".repeat(128))) {
            assertThrows(IllegalArgumentException::class.java) {
                client.transactions().cancelCheckout("payment", CancellationRequest("stop"), key)
            }
        }
        for (amount in listOf(Double.NaN, Double.POSITIVE_INFINITY, 0.0, -1.0)) {
            assertThrows(IllegalArgumentException::class.java) { client.payouts().quote(PayoutQuoteRequest("method", amount)) }
        }
    }

    private fun query(uri: URI): Map<String, String> = uri.rawQuery?.split("&")?.associate {
        val parts = it.split("=", limit = 2)
        val value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
        parts[0] to if (parts[0] == "amount") value.toDouble().toString() else value
    } ?: emptyMap()

    private fun assertJson(expected: JsonNode, actual: JsonNode) {
        when {
            expected.isNumber -> assertEquals(expected.asDouble(), actual.asDouble())
            expected.isObject -> expected.fields().forEach { (key, value) ->
                assertTrue(actual.has(key), key)
                assertJson(value, actual[key])
            }
            expected.isArray -> {
                assertEquals(expected.size(), actual.size())
                for (index in 0 until expected.size()) assertJson(expected[index], actual[index])
            }
            else -> assertEquals(expected, actual)
        }
    }
}
