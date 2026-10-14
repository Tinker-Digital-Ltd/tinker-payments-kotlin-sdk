package co.ke.tinker.webhook

import co.ke.tinker.exception.InvalidPayloadException
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class OnboardingWebhookTest {
    @Test
    fun verifiesGoSetupTestWithoutCreatingPayment() {
        val payload = requireNotNull(javaClass.getResource("/setup-webhook.json")).readText()
        val handler = WebhookHandler()
        val event = handler.handle(payload)
        assertTrue(event.isOnboardingEvent)
        assertTrue(event.isSetupTest)
        assertTrue(requireNotNull(event.onboardingData).message.contains("webhook verification"))
        assertNull(event.paymentData)
        assertNull(handler.handleAsTransaction(payload))
        assertTrue(handler.verifySignature(event, "webhook-secret"))
        assertTrue(handler.verifySignature(payload, "webhook-secret"))
        assertFalse(handler.verifySignature(payload, "wrong-secret"))
        assertFalse(handler.verifySignature(payload, ""))
        for ((before, after) in listOf("verification" to "forged", "HMAC-SHA256" to "none", "sha256=" to "invalid=")) {
            assertFalse(handler.verifySignature(payload.replace(before, after), "webhook-secret"))
        }
        assertFalse(handler.handle(payload.replace("setup.webhook_test", "onboarding.future")).isSetupTest)
        val unknown = payload.replace("\"source\":\"onboarding\"", "\"source\":\"unknown\"")
        assertThrows(InvalidPayloadException::class.java) { handler.handle(unknown) }
    }
}
