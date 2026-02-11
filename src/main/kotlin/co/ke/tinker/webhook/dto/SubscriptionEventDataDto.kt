package co.ke.tinker.webhook.dto

data class SubscriptionEventDataDto(
    val id: String?,
    val status: String?,
    val planId: String?,
    val accountId: String?,
    val currentPeriodStart: String?,
    val currentPeriodEnd: String?,
    val createdAt: String?,
    val cancelledAt: String?,
    val pausedAt: String?,
    val reactivatedAt: String?
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): SubscriptionEventDataDto {
            val id = (data["subscription_id"] ?: data["id"]) as? String
            val status = data["status"] as? String ?: ""
            val planId = data["plan_id"] as? String ?: ""
            val accountId = (data["account_id"] ?: data["customer_id"]) as? String ?: ""
            val currentPeriodStart = data["current_period_start"] as? String
            val currentPeriodEnd = data["current_period_end"] as? String
            val createdAt = data["created_at"] as? String ?: currentPeriodStart ?: ""
            val cancelledAt = data["cancelled_at"] as? String
            val pausedAt = data["paused_at"] as? String
            val reactivatedAt = data["reactivated_at"] as? String
            return SubscriptionEventDataDto(id, status, planId, accountId, currentPeriodStart, currentPeriodEnd, createdAt, cancelledAt, pausedAt, reactivatedAt)
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "subscription_id" to id,
        "status" to status,
        "plan_id" to planId,
        "account_id" to accountId,
        "current_period_start" to currentPeriodStart,
        "current_period_end" to currentPeriodEnd,
        "created_at" to createdAt,
        "cancelled_at" to cancelledAt,
        "paused_at" to pausedAt,
        "reactivated_at" to reactivatedAt
    )
}
