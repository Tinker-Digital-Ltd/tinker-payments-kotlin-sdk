package co.ke.tinker.model

import co.ke.tinker.enums.PaymentStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TransactionTest {
    @Test
    fun testInitializeWithInitiationData() {
        val data = mapOf(
            "payment_reference" to "TXN-123",
            "status" to "pending",
            "authorization_url" to "https://example.com/auth"
        )

        val transaction = Transaction(data)

        assertNotNull(transaction.initiationData)
        assertNull(transaction.queryData)
        assertNull(transaction.callbackData)
        assertEquals(PaymentStatus.PENDING, transaction.status)
    }

    @Test
    fun testInitializeWithQueryCallbackData() {
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

    @Test
    fun testSuccessful() {
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
        assertTrue(transaction.isSuccessful)
        assertFalse(transaction.isPending)
        assertFalse(transaction.isCancelled)
        assertFalse(transaction.isFailed)
    }

    @Test
    fun testPending() {
        val data = mapOf(
            "payment_reference" to "TXN-123",
            "status" to "pending"
        )

        val transaction = Transaction(data)
        assertTrue(transaction.isPending)
        assertFalse(transaction.isSuccessful)
        assertFalse(transaction.isCancelled)
        assertFalse(transaction.isFailed)
    }

    @Test
    fun testCancelled() {
        val data = mapOf(
            "id" to "123",
            "reference" to "REF-123",
            "status" to "cancelled",
            "amount" to 100.0,
            "currency" to "KES",
            "created_at" to "2024-01-01T00:00:00Z",
            "channel" to "mpesa"
        )

        val transaction = Transaction(data)
        assertTrue(transaction.isCancelled)
        assertFalse(transaction.isSuccessful)
        assertFalse(transaction.isPending)
        assertFalse(transaction.isFailed)
    }

    @Test
    fun testFailed() {
        val data = mapOf(
            "id" to "123",
            "reference" to "REF-123",
            "status" to "failed",
            "amount" to 100.0,
            "currency" to "KES",
            "created_at" to "2024-01-01T00:00:00Z",
            "channel" to "mpesa"
        )

        val transaction = Transaction(data)
        assertTrue(transaction.isFailed)
        assertFalse(transaction.isSuccessful)
        assertFalse(transaction.isPending)
        assertFalse(transaction.isCancelled)
    }
}


