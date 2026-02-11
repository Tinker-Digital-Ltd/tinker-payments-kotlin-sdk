package co.ke.tinker.webhook

import co.ke.tinker.exception.InvalidPayloadException
import co.ke.tinker.exception.WebhookException
import co.ke.tinker.model.Transaction
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class WebhookHandler {
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    fun handle(payload: String): WebhookEvent {
        val data: Map<String, Any?>
        try {
            data = objectMapper.readValue(payload)
        } catch (e: Exception) {
            throw InvalidPayloadException("Invalid JSON payload: ${e.message}")
        }
        return handle(data)
    }

    fun handle(payload: Map<String, Any?>): WebhookEvent {
        if (payload.isEmpty()) {
            throw InvalidPayloadException("Webhook payload must be a hash")
        }
        return WebhookEvent(payload)
    }

    fun handleFromRequest(requestBody: String): WebhookEvent {
        if (requestBody.isBlank()) {
            throw WebhookException("Unable to read request body")
        }
        return handle(requestBody)
    }

    fun handleAsTransaction(payload: String): Transaction? = handle(payload).toTransaction()
    fun handleAsTransaction(payload: Map<String, Any?>): Transaction? = handle(payload).toTransaction()

    fun verifySignature(event: WebhookEvent, webhookSecret: String): Boolean {
        if (webhookSecret.isBlank()) {
            return false
        }

        val signature = event.security.signature
        if (signature == null || !signature.startsWith("sha256=")) {
            return false
        }

        return try {
            val payloadWithoutSecurity = linkedMapOf<String, Any?>(
                "id" to event.id,
                "type" to event.type,
                "source" to event.source,
                "timestamp" to event.timestamp,
                "data" to event.rawData,
                "meta" to event.rawMeta
            )
            val encoded = objectMapper.writeValueAsString(payloadWithoutSecurity)
            val computed = "sha256=${hmacSha256Hex(encoded, webhookSecret)}"
            signature == computed
        } catch (_: Exception) {
            false
        }
    }

    fun verifySignature(payload: String, webhookSecret: String): Boolean = verifySignature(handle(payload), webhookSecret)
    fun verifySignature(payload: Map<String, Any?>, webhookSecret: String): Boolean = verifySignature(handle(payload), webhookSecret)

    private fun hmacSha256Hex(payload: String, secret: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        mac.init(secretKey)
        val hash = mac.doFinal(payload.toByteArray(StandardCharsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}
