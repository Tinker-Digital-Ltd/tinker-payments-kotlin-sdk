package co.ke.tinker.webhook

import co.ke.tinker.exception.InvalidPayloadException
import co.ke.tinker.exception.WebhookException
import co.ke.tinker.model.Transaction
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

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

    fun handleAsTransaction(payload: String): Transaction? {
        val event = handle(payload)
        return event.toTransaction()
    }

    fun handleAsTransaction(payload: Map<String, Any?>): Transaction? {
        val event = handle(payload)
        return event.toTransaction()
    }
}


