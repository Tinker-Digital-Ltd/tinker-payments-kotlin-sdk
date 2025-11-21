package co.ke.tinker.webhook

import co.ke.tinker.exception.InvalidPayloadException
import co.ke.tinker.exception.ExceptionCode
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
    
    init {
        this.id = payload["id"] as? String
        this.type = payload["type"] as? String
        this.source = payload["source"] as? String
        this.timestamp = payload["timestamp"] as? String
        
        val dataMap = payload["data"] as? Map<String, Any?>
        this.data = createEventData(dataMap, this.source)
        
        val metaMap = payload["meta"] as? Map<String, Any?>
        this.meta = WebhookMeta.fromMap(metaMap)
        
        val securityMap = payload["security"] as? Map<String, Any?>
        this.security = WebhookSecurity.fromMap(securityMap)
    }
    
    val isPaymentEvent: Boolean
        get() = source == "payment"
    
    val isSubscriptionEvent: Boolean
        get() = source == "subscription"
    
    val isInvoiceEvent: Boolean
        get() = source == "invoice"
    
    val isSettlementEvent: Boolean
        get() = source == "settlement"
    
    val paymentData: PaymentEventDataDto?
        get() = data as? PaymentEventDataDto
    
    val subscriptionData: SubscriptionEventDataDto?
        get() = data as? SubscriptionEventDataDto
    
    val invoiceData: InvoiceEventDataDto?
        get() = data as? InvoiceEventDataDto
    
    val settlementData: SettlementEventDataDto?
        get() = data as? SettlementEventDataDto
    
    fun toTransaction(): Transaction? {
        if (!isPaymentEvent) {
            return null
        }
        val paymentData = this.paymentData ?: return null
        return Transaction(paymentData.toMap())
    }
    
    private fun createEventData(data: Map<String, Any?>?, source: String?): Any? {
        if (data == null) {
            return null
        }
        return when (source) {
            "payment" -> PaymentEventDataDto.fromMap(data)
            "subscription" -> SubscriptionEventDataDto.fromMap(data)
            "invoice" -> InvoiceEventDataDto.fromMap(data)
            "settlement" -> SettlementEventDataDto.fromMap(data)
            else -> throw InvalidPayloadException(
                "Unknown webhook source: $source",
                ExceptionCode.INVALID_PAYLOAD
            )
        }
    }
}

