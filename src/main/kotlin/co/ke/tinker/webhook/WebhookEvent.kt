package co.ke.tinker.webhook

import co.ke.tinker.exception.ExceptionCode
import co.ke.tinker.exception.InvalidPayloadException
import co.ke.tinker.model.Transaction
import co.ke.tinker.webhook.dto.InvoiceEventDataDto
import co.ke.tinker.webhook.dto.PaymentEventDataDto
import co.ke.tinker.webhook.dto.SettlementEventDataDto
import co.ke.tinker.webhook.dto.SubscriptionEventDataDto

class WebhookEvent(payload: Map<String, Any?>) {
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
        return when (source) {
            "payment" -> PaymentEventDataDto.fromMap(data)
            "subscription" -> SubscriptionEventDataDto.fromMap(data)
            "invoice" -> InvoiceEventDataDto.fromMap(data)
            "settlement" -> SettlementEventDataDto.fromMap(data)
            else -> throw InvalidPayloadException("Unknown webhook source: $source", ExceptionCode.INVALID_PAYLOAD)
        }
    }
}
