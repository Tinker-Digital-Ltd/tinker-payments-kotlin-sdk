package co.ke.tinker.webhook

import co.ke.tinker.model.lifecycle.CancellationStatus
import co.ke.tinker.model.lifecycle.PayoutStatus
import co.ke.tinker.model.lifecycle.RefundStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LifecycleWebhookTest {
    @Test
    fun `signed lifecycle evidence never becomes payment transactions`() {
        val handler = WebhookHandler()
        for (fixture in listOf("checkout-cancellation-webhook.json", "refund-webhook.json", "withdrawal-webhook.json")) {
            val payload = requireNotNull(javaClass.getResource("/$fixture")).readText()
            val event = handler.handle(payload)
            assertTrue(handler.verifySignature(event, "webhook-secret"), fixture)
            when (fixture) {
                "checkout-cancellation-webhook.json" -> {
                    assertTrue(event.isCheckoutCancellationEvent)
                    assertEquals(CancellationStatus.OUTCOME_UNKNOWN, event.cancellationData?.status)
                    assertNotNull(event.cancellationData?.paymentId)
                }
                "refund-webhook.json" -> {
                    assertTrue(event.isRefundEvent)
                    assertEquals(RefundStatus.PENDING_REVIEW, event.refundData?.status)
                    assertEquals("app-1", event.refundData?.requestedByAppId)
                }
                "withdrawal-webhook.json" -> {
                    assertTrue(event.isWithdrawalEvent)
                    assertEquals(PayoutStatus.PENDING_APPROVAL, event.withdrawalData?.status)
                    assertEquals(98.0, event.withdrawalData?.netAmount)
                }
                else -> error("Unhandled fixture $fixture")
            }
            assertNull(event.paymentData)
            assertNull(event.toTransaction())
            assertNull(handler.handleAsTransaction(payload))
            assertFalse(handler.verifySignature(event, "wrong-secret"))
            assertFalse(handler.verifySignature(payload.replace("app-1", "forged"), "webhook-secret"))
            val future = handler.handle(payload.replace(".updated.v1", ".updated.v2"))
            assertFalse(future.isCheckoutCancellationEvent)
            assertFalse(future.isRefundEvent)
            assertFalse(future.isWithdrawalEvent)
            assertNull(future.toTransaction())
        }
    }
}
