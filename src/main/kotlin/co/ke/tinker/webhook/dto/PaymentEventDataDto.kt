package co.ke.tinker.webhook.dto

import co.ke.tinker.enums.PaymentStatus

data class PaymentEventDataDto(
    val id: String?,
    val status: PaymentStatus,
    val reference: String?,
    val amount: Double?,
    val currency: String?,
    val channel: String?,
    val createdAt: String?,
    val paidAt: String?
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): PaymentEventDataDto {
            val id = data["id"] as? String
            val statusValue = data["status"] as? String ?: "pending"
            val status = PaymentStatus.fromString(statusValue)
            val reference = data["reference"] as? String
            val amountObj = data["amount"]
            val amount = amountObj?.let { (it as? Number)?.toDouble() }
            val currency = data["currency"] as? String
            val channel = data["channel"] as? String
            val createdAt = data["created_at"] as? String
            val paidAt = data["paid_at"] as? String
            return PaymentEventDataDto(id, status, reference, amount, currency, channel, createdAt, paidAt)
        }
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "status" to status.value,
            "reference" to reference,
            "amount" to amount,
            "currency" to currency,
            "channel" to channel,
            "created_at" to createdAt,
            "paid_at" to paidAt
        )
    }
}

