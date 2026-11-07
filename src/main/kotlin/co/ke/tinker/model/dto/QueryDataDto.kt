package co.ke.tinker.model.dto

import co.ke.tinker.enums.PaymentStatus

data class QueryDataDto(
    val id: String?,
    val status: PaymentStatus,
    val reference: String?,
    val amount: Double?,
    val currency: String?,
    val paidAt: String?,
    val createdAt: String?,
    val channel: String?,
    val rawStatus: String = status.value,
    val country: String? = null,
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): QueryDataDto {
            val id = data["id"] as? String
            val statusValue = PaymentStatus.normalize(data["status"] as? String)
            val status = PaymentStatus.fromString(statusValue)
            val reference = data["reference"] as? String
            val amountObj = data["amount"]
            val amount = amountObj?.let { (it as? Number)?.toDouble() }
            val currency = data["currency"] as? String
            val paidAt = data["paid_at"] as? String
            val createdAt = data["created_at"] as? String
            val channel = data["channel"] as? String
            return QueryDataDto(id, status, reference, amount, currency, paidAt, createdAt, channel, statusValue, data["country"] as? String)
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf<String, Any?>(
            "id" to id,
            "status" to rawStatus,
            "reference" to reference,
            "amount" to amount,
            "currency" to currency,
            "paid_at" to paidAt,
            "created_at" to createdAt,
            "channel" to channel,
        ) + listOfNotNull(country?.let { "country" to it })
    }
}
