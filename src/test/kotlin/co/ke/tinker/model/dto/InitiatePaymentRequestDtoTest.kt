package co.ke.tinker.model.dto

import co.ke.tinker.enums.Gateway
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class InitiatePaymentRequestDtoTest {
    @Test
    fun testSerializationExcludesNullOptionalFields() {
        val dto = InitiatePaymentRequestDto(
            amount = 100.00,
            currency = "KES",
            gateway = Gateway.MPESA,
            merchantReference = "ORDER-12345",
            returnUrl = "https://example.com/return"
        )

        val map = dto.toMap()

        assertTrue(map.containsKey("amount"))
        assertTrue(map.containsKey("currency"))
        assertTrue(map.containsKey("gateway"))
        assertTrue(map.containsKey("merchantReference"))
        assertTrue(map.containsKey("returnUrl"))
        assertFalse(map.containsKey("customerPhone"))
        assertFalse(map.containsKey("customerEmail"))
        assertFalse(map.containsKey("transactionDesc"))
        assertFalse(map.containsKey("metadata"))
    }

    @Test
    fun testSerializationIncludesOptionalFieldsWhenProvided() {
        val metadata = mapOf("order_id" to "12345")

        val dto = InitiatePaymentRequestDto(
            amount = 100.00,
            currency = "KES",
            gateway = Gateway.PAYSTACK,
            merchantReference = "ORDER-12345",
            returnUrl = "https://example.com/return",
            customerPhone = "+254712345678",
            customerEmail = "customer@example.com",
            transactionDesc = "Payment for order",
            metadata = metadata
        )

        val map = dto.toMap()

        assertTrue(map.containsKey("customerPhone"))
        assertTrue(map.containsKey("customerEmail"))
        assertTrue(map.containsKey("transactionDesc"))
        assertTrue(map.containsKey("metadata"))
    }
}

