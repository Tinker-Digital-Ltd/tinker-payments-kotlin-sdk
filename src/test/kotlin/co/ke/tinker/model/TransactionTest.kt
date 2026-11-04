package co.ke.tinker.model

import co.ke.tinker.enums.Gateway
import co.ke.tinker.enums.PaymentStatus
import co.ke.tinker.model.dto.InitiatePaymentRequestDto
import co.ke.tinker.model.dto.InitiationDataDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TransactionTest {

    @Test
    fun `serializes paymentMethod and parses wallet initiation fields`() {
        val request = InitiatePaymentRequestDto(
            amount = 100.0,
            currency = "USD",
            gateway = Gateway.STRIPE,
            merchantReference = "ORDER-1",
            returnUrl = "https://example.com/return",
            paymentMethod = "google_pay",
        )
        assertEquals("google_pay", request.toMap()["paymentMethod"])
        assertFalse(request.copy(paymentMethod = null).toMap().containsKey("paymentMethod"))

        val data = InitiationDataDto.fromMap(
            mapOf(
                "paymentReference" to "pi_123",
                "clientSecret" to "pi_123_secret_abc",
                "publishableKey" to "pk_test_abc",
            ),
        )
        assertEquals("pi_123_secret_abc", data.clientSecret)
        assertEquals("pk_test_abc", data.publishableKey)
        assertNull(data.authorizationUrl)
    }

    @Test
    fun `initializes with initiation data using camelCase keys`() {
        val data = mapOf(
            "paymentReference" to "TXN-123",
            "status" to "pending",
            "authorizationUrl" to "https://example.com/auth",
        )

        val transaction = Transaction(data)

        assertNotNull(transaction.initiationData)
        assertNull(transaction.queryData)
        assertEquals(PaymentStatus.PENDING, transaction.status)
    }

    @Test
    fun `initializes with query and callback data`() {
        val data = mapOf(
            "id" to "123",
            "reference" to "REF-123",
            "status" to "success",
            "amount" to 100.0,
            "currency" to "KES",
            "created_at" to "2024-01-01T00:00:00Z",
            "channel" to "mpesa",
        )

        val transaction = Transaction(data)

        assertNotNull(transaction.queryData)
        assertNotNull(transaction.callbackData)
        assertNull(transaction.initiationData)
        assertEquals(PaymentStatus.SUCCESS, transaction.status)
    }
}
