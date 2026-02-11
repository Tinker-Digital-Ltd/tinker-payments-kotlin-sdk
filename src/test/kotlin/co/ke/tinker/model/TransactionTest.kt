package co.ke.tinker.model

import co.ke.tinker.enums.PaymentStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TransactionTest {

    @Test
    fun `initializes with initiation data using camelCase keys`() {
        val data = mapOf(
            "paymentReference" to "TXN-123",
            "status" to "pending",
            "authorizationUrl" to "https://example.com/auth"
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
            "channel" to "mpesa"
        )

        val transaction = Transaction(data)

        assertNotNull(transaction.queryData)
        assertNotNull(transaction.callbackData)
        assertNull(transaction.initiationData)
        assertEquals(PaymentStatus.SUCCESS, transaction.status)
    }
}
