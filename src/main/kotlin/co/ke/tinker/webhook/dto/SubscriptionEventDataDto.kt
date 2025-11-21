package co.ke.tinker.webhook.dto

data class SubscriptionEventDataDto(
    val id: String?,
    val status: String?,
    val planId: String?,
    val customerId: String?,
    val createdAt: String?,
    val cancelledAt: String?,
    val pausedAt: String?,
    val reactivatedAt: String?
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): SubscriptionEventDataDto {
            val id = data["id"] as? String
            val status = data["status"] as? String
            val planId = data["plan_id"] as? String ?: ""
            val customerId = data["customer_id"] as? String ?: ""
            val createdAt = data["created_at"] as? String ?: ""
            val cancelledAt = data["cancelled_at"] as? String
            val pausedAt = data["paused_at"] as? String
            val reactivatedAt = data["reactivated_at"] as? String
            return SubscriptionEventDataDto(id, status, planId, customerId, createdAt, cancelledAt, pausedAt, reactivatedAt)
        }
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "status" to status,
            "plan_id" to planId,
            "customer_id" to customerId,
            "created_at" to createdAt,
            "cancelled_at" to cancelledAt,
            "paused_at" to pausedAt,
            "reactivated_at" to reactivatedAt
        )
    }
}

