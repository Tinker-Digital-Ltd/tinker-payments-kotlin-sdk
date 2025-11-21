package co.ke.tinker.enums

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class PaymentStatusTest {
    @Test
    fun testFromString() {
        assertEquals(PaymentStatus.PENDING, PaymentStatus.fromString("pending"))
        assertEquals(PaymentStatus.SUCCESS, PaymentStatus.fromString("success"))
        assertEquals(PaymentStatus.CANCELLED, PaymentStatus.fromString("cancelled"))
        assertEquals(PaymentStatus.FAILED, PaymentStatus.fromString("failed"))
    }

    @Test
    fun testFromStringWithNull() {
        assertEquals(PaymentStatus.PENDING, PaymentStatus.fromString(null))
    }

    @Test
    fun testFromStringWithInvalidValue() {
        assertEquals(PaymentStatus.PENDING, PaymentStatus.fromString("invalid"))
    }

    @Test
    fun testValue() {
        assertEquals("pending", PaymentStatus.PENDING.value)
        assertEquals("success", PaymentStatus.SUCCESS.value)
        assertEquals("cancelled", PaymentStatus.CANCELLED.value)
        assertEquals("failed", PaymentStatus.FAILED.value)
    }
}

