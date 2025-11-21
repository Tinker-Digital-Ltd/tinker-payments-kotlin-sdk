package co.ke.tinker.webhook.dto

data class SettlementEventDataDto(
    val id: String?,
    val status: String?,
    val amount: Double?,
    val currency: String?,
    val settlementDate: String?,
    val createdAt: String?,
    val processedAt: String?
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): SettlementEventDataDto {
            val id = data["id"] as? String
            val status = data["status"] as? String
            val amountObj = data["amount"]
            val amount = amountObj?.let { (it as? Number)?.toDouble() }
            val currency = data["currency"] as? String
            val settlementDate = data["settlement_date"] as? String ?: ""
            val createdAt = data["created_at"] as? String ?: ""
            val processedAt = data["processed_at"] as? String
            return SettlementEventDataDto(id, status, amount, currency, settlementDate, createdAt, processedAt)
        }
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "status" to status,
            "amount" to amount,
            "currency" to currency,
            "settlement_date" to settlementDate,
            "created_at" to createdAt,
            "processed_at" to processedAt
        )
    }
}

