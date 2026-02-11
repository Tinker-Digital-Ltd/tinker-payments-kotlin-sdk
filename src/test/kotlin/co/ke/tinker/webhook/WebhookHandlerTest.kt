package co.ke.tinker.webhook

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WebhookHandlerTest {
    private val handler = WebhookHandler()

    @Test
    fun `handles payment payload and verifies signature`() {
        val unsigned = linkedMapOf<String, Any?>(
            "id" to "evt_123",
            "type" to "payment.completed",
            "source" to "payment",
            "timestamp" to "2026-02-09T14:30:00Z",
            "data" to mapOf("reference" to "TXN-1", "status" to "success"),
            "meta" to mapOf("app_id" to "app_123", "version" to "1.0")
        )

        val secret = "test_secret"
        val json = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper().writeValueAsString(unsigned)
        val mac = javax.crypto.Mac.getInstance("HmacSHA256")
        mac.init(javax.crypto.spec.SecretKeySpec(secret.toByteArray(), "HmacSHA256"))
        val sig = "sha256=" + mac.doFinal(json.toByteArray()).joinToString("") { "%02x".format(it) }

        val payload = unsigned + mapOf("security" to mapOf("signature" to sig, "algorithm" to "HMAC-SHA256"))
        val event = handler.handle(payload)

        assertTrue(event.isPaymentEvent)
        assertTrue(handler.verifySignature(event, secret))
    }
}
