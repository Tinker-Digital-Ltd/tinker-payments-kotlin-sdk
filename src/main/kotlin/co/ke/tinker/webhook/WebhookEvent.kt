package co.ke.tinker.webhook

import co.ke.tinker.exception.ExceptionCode
import co.ke.tinker.exception.InvalidPayloadException
import co.ke.tinker.model.Transaction
import co.ke.tinker.model.lifecycle.Cancellation
import co.ke.tinker.model.lifecycle.Payout
import co.ke.tinker.model.lifecycle.Refund
import co.ke.tinker.webhook.dto.InvoiceEventDataDto
import co.ke.tinker.webhook.dto.OnboardingEventDataDto
import co.ke.tinker.webhook.dto.PaymentEventDataDto
import co.ke.tinker.webhook.dto.SettlementEventDataDto
import co.ke.tinker.webhook.dto.SubscriptionEventDataDto
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class WebhookEvent(payload: Map<String, Any?>) {
    private companion object {
        val lifecycleMapper = jacksonObjectMapper()
    }

    val id: String?
    val type: String?
    val source: String?
    val timestamp: String?
    private val data: Any?
    val meta: WebhookMeta
    val security: WebhookSecurity
    val rawData: Map<String, Any?>
    val rawMeta: Map<String, Any?>

    init {
        id = payload["id"] as? String
        type = payload["type"] as? String
        source = payload["source"] as? String
        timestamp = payload["timestamp"] as? String
        rawData = payload["data"] as? Map<String, Any?> ?: emptyMap()
        rawMeta = payload["meta"] as? Map<String, Any?> ?: emptyMap()
        data = createEventData(rawData, source)
        meta = WebhookMeta.fromMap(rawMeta)
        security = WebhookSecurity.fromMap(payload["security"] as? Map<String, Any?>)
    }

    val isOnboardingEvent: Boolean get() = source == "onboarding"

    // Verify the signature before acknowledging this test.
    val isSetupTest: Boolean get() = isOnboardingEvent && type == "setup.webhook_test"
    val onboardingData: OnboardingEventDataDto? get() = data as? OnboardingEventDataDto

    // Verify the signature before consuming this typed lifecycle data.
    val isCheckoutCancellationEvent: Boolean get() = source == "payment" && type == "checkout.cancellation.updated.v1"
    val cancellationData: Cancellation? get() = data as? Cancellation
    val isRefundEvent: Boolean get() = source == "payment" && type == "refund.updated.v1"
    val refundData: Refund? get() = data as? Refund
    val isWithdrawalEvent: Boolean get() = source == "payment" && type == "withdrawal.updated.v1"
    val withdrawalData: Payout? get() = data as? Payout
    val isPaymentEvent: Boolean get() = source == "payment"
    val isSubscriptionEvent: Boolean get() = source == "subscription"
    val isInvoiceEvent: Boolean get() = source == "invoice"
    val isSettlementEvent: Boolean get() = source == "settlement"

    val paymentData: PaymentEventDataDto? get() = data as? PaymentEventDataDto
    val subscriptionData: SubscriptionEventDataDto? get() = data as? SubscriptionEventDataDto
    val invoiceData: InvoiceEventDataDto? get() = data as? InvoiceEventDataDto
    val settlementData: SettlementEventDataDto? get() = data as? SettlementEventDataDto

    fun toTransaction(): Transaction? {
        if (!isPaymentEvent) {
            return null
        }
        val payment = paymentData ?: return null
        return Transaction(payment.toMap())
    }

    private fun createEventData(data: Map<String, Any?>, source: String?): Any {
        // Lifecycle resources and unknown versions stay separate from payment transactions.
        if (source == "payment") {
            when (type) {
                "checkout.cancellation.updated.v1" -> return lifecycleMapper.convertValue<Cancellation>(requireNotNull(data["resource"]))
                "refund.updated.v1" -> return lifecycleMapper.convertValue<Refund>(requireNotNull(data["resource"]))
                "withdrawal.updated.v1" -> return lifecycleMapper.convertValue<Payout>(requireNotNull(data["resource"]))
            }
            if (type?.startsWith("checkout.cancellation.") == true || type?.startsWith("refund.") == true || type?.startsWith("withdrawal.") == true) return data
        }
        return when (source) {
            "onboarding" -> OnboardingEventDataDto.fromMap(data)
            "payment" -> PaymentEventDataDto.fromMap(data)
            "subscription" -> SubscriptionEventDataDto.fromMap(data)
            "invoice" -> InvoiceEventDataDto.fromMap(data)
            "settlement" -> SettlementEventDataDto.fromMap(data)
            else -> throw InvalidPayloadException("Unknown webhook source: $source", ExceptionCode.INVALID_PAYLOAD)
        }
    }
}
