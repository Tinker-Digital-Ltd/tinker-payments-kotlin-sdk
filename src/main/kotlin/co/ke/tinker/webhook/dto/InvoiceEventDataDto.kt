package co.ke.tinker.webhook.dto

data class InvoiceEventDataDto(
    val id: String?,
    val status: String?,
    val invoiceNumber: String?,
    val amount: Double?,
    val currency: String?,
    val subscriptionId: String?,
    val createdAt: String?,
    val paidAt: String?
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): InvoiceEventDataDto {
            val id = (data["invoice_id"] ?: data["id"]) as? String
            val status = data["status"] as? String ?: ""
            val invoiceNumber = data["invoice_number"] as? String ?: ""
            val amount = (data["amount"] as? Number)?.toDouble() ?: 0.0
            val currency = data["currency"] as? String ?: ""
            val subscriptionId = data["subscription_id"] as? String ?: ""
            val createdAt = data["created_at"] as? String ?: ""
            val paidAt = data["paid_at"] as? String
            return InvoiceEventDataDto(id, status, invoiceNumber, amount, currency, subscriptionId, createdAt, paidAt)
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "invoice_id" to id,
        "status" to status,
        "invoice_number" to invoiceNumber,
        "amount" to amount,
        "currency" to currency,
        "subscription_id" to subscriptionId,
        "created_at" to createdAt,
        "paid_at" to paidAt
    )
}
