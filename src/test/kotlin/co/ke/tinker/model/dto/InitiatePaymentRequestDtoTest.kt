package co.ke.tinker.model.dto

import co.ke.tinker.enums.Gateway
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InitiatePaymentRequestDtoTest {

    @Test
    fun `converts required fields`() {
        val dto = InitiatePaymentRequestDto(
            amount = 100.0,
            currency = "KES",
            gateway = Gateway.MPESA,
            merchantReference = "ORDER-123",
            returnUrl = "https://example.com/return",
        )

        val map = dto.toMap()

        assertEquals("ORDER-123", map["merchantReference"])
        assertEquals("mpesa", map["gateway"])
        assertFalse(map.containsKey("metadata"))
    }

    @Test
    fun `includes optional metadata`() {
        val dto = InitiatePaymentRequestDto(
            amount = 100.0,
            currency = "KES",
            gateway = Gateway.MPESA,
            merchantReference = "ORDER-123",
            returnUrl = "https://example.com/return",
            metadata = mapOf("order_id" to "12345"),
        )

        val map = dto.toMap()
        assertTrue(map.containsKey("metadata"))
    }

    @Test
    fun `sends customer name and country only when set and keeps unknown gateways`() {
        val dto = InitiatePaymentRequestDto(100.0, "EUR", "futurepay", "ORDER-1", "https://example.com/return", customerName = "Ada", country = "DE")
        assertEquals("Ada", dto.toMap()["customerName"])
        assertEquals("DE", dto.toMap()["country"])
        assertEquals("futurepay", dto.gateway)
        val bare = InitiatePaymentRequestDto(100.0, "EUR", "futurepay", "ORDER-1", "https://example.com/return").toMap()
        assertFalse(bare.containsKey("customerName"))
        assertFalse(bare.containsKey("country"))
    }

    @Test
    fun `payment data carries country only when sent`() {
        val data = mapOf<String, Any?>("id" to "1", "status" to "authorised", "reference" to "R")
        assertEquals("DE", QueryDataDto.fromMap(data + ("country" to "DE")).toMap()["country"])
        assertFalse(QueryDataDto.fromMap(data).toMap().containsKey("country"))
        assertEquals("DE", CallbackDataDto.fromMap(data + ("country" to "DE")).country)
        assertEquals("DE", co.ke.tinker.webhook.dto.PaymentEventDataDto.fromMap(data + ("country" to "DE")).toMap()["country"])
    }
}
