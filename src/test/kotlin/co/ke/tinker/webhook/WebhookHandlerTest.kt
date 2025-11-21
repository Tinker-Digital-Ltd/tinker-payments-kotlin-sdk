package co.ke.tinker.webhook

import co.ke.tinker.exception.InvalidPayloadException
import co.ke.tinker.model.Transaction
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WebhookHandlerTest {
    private val handler = WebhookHandler()
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    @Test
    fun testHandleJsonStringPayload() {
        val payloadMap = mapOf(
            "id" to "evt_123",
            "type" to "payment.completed",
            "source" to "payment",
            "timestamp" to "2024-01-01T00:00:00Z",
            "data" to mapOf(
                "id" to "123",
                "status" to "success",
                "reference" to "REF-123",
                "amount" to 100.0,
                "currency" to "KES",
                "channel" to "mpesa",
                "created_at" to "2024-01-01T00:00:00Z"
            ),
            "meta" to mapOf("app_id" to "app_123"),
            "security" to mapOf("signature" to "sig_123")
        )

        val payload = objectMapper.writeValueAsString(payloadMap)
        val event = handler.handle(payload)

        assertNotNull(event)
        assertTrue(event.isPaymentEvent)
        assertEquals("evt_123", event.id)
        assertEquals("payment.completed", event.type)
    }

    @Test
    fun testHandleMapPayload() {
        val payload = mapOf(
            "id" to "evt_123",
            "type" to "payment.completed",
            "source" to "payment",
            "timestamp" to "2024-01-01T00:00:00Z",
            "data" to mapOf(
                "id" to "123",
                "status" to "success",
                "reference" to "REF-123",
                "amount" to 100.0,
                "currency" to "KES",
                "channel" to "mpesa",
                "created_at" to "2024-01-01T00:00:00Z"
            ),
            "meta" to mapOf("app_id" to "app_123"),
            "security" to mapOf("signature" to "sig_123")
        )

        val event = handler.handle(payload)

        assertNotNull(event)
        assertTrue(event.isPaymentEvent)
    }

    @Test
    fun testHandleInvalidJson() {
        assertThrows(InvalidPayloadException::class.java) {
            handler.handle("invalid json")
        }
    }

    @Test
    fun testHandleAsTransaction() {
        val payloadMap = mapOf(
            "id" to "evt_123",
            "type" to "payment.completed",
            "source" to "payment",
            "timestamp" to "2024-01-01T00:00:00Z",
            "data" to mapOf(
                "id" to "123",
                "status" to "success",
                "reference" to "REF-123",
                "amount" to 100.0,
                "currency" to "KES",
                "channel" to "mpesa",
                "created_at" to "2024-01-01T00:00:00Z"
            ),
            "meta" to mapOf("app_id" to "app_123"),
            "security" to mapOf("signature" to "sig_123")
        )

        val payload = objectMapper.writeValueAsString(payloadMap)
        val transaction = handler.handleAsTransaction(payload)

        assertNotNull(transaction)
        assertTrue(transaction?.isSuccessful == true)
    }

    @Test
    fun testHandleAsTransactionNonPaymentEvent() {
        val payloadMap = mapOf(
            "id" to "evt_123",
            "type" to "subscription.created",
            "source" to "subscription",
            "timestamp" to "2024-01-01T00:00:00Z",
            "data" to mapOf(
                "id" to "123",
                "status" to "active"
            ),
            "meta" to mapOf("app_id" to "app_123"),
            "security" to mapOf("signature" to "sig_123")
        )

        val payload = objectMapper.writeValueAsString(payloadMap)
        val transaction = handler.handleAsTransaction(payload)

        assertNull(transaction)
    }
}


