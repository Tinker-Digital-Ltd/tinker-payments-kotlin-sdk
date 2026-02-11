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
            returnUrl = "https://example.com/return"
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
            metadata = mapOf("order_id" to "12345")
        )

        val map = dto.toMap()
        assertTrue(map.containsKey("metadata"))
    }
}
