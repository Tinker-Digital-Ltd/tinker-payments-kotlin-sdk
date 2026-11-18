package co.ke.tinker.webhook.dto

data class SubscriptionEventDataDto @JvmOverloads constructor(
    val id: String?,
    val status: String?,
    val planId: String?,
    val accountId: String?,
    val currentPeriodStart: String?,
    val currentPeriodEnd: String?,
    val createdAt: String?,
    val cancelledAt: String?,
    val pausedAt: String?,
    val reactivatedAt: String?,
    val billingStatus: String? = null,
    val trialEndsAt: String? = null,
    val productId: String? = null,
    val entitlementIds: List<String> = emptyList(),
    val store: String? = null,
    val periodType: String? = null,
    val expirationAt: String? = null,
    val gracePeriodExpirationAt: String? = null,
    val externalCustomerId: String? = null,
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
            return SubscriptionEventDataDto(id, status, planId, accountId, currentPeriodStart, currentPeriodEnd, createdAt, cancelledAt, pausedAt, reactivatedAt, data["billing_status"] as? String, data["trial_ends_at"] as? String, data["product_id"] as? String, (data["entitlement_ids"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(), data["store"] as? String, data["period_type"] as? String, data["expiration_at"] as? String, data["grace_period_expiration_at"] as? String, data["external_customer_id"] as? String)
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "subscription_id" to id,
        "status" to status,
        "plan_id" to planId,
        "account_id" to accountId,
        "external_customer_id" to externalCustomerId,
        "current_period_start" to currentPeriodStart,
        "current_period_end" to currentPeriodEnd,
        "created_at" to createdAt,
        "cancelled_at" to cancelledAt,
        "paused_at" to pausedAt,
        "reactivated_at" to reactivatedAt,
        "billing_status" to billingStatus,
        "trial_ends_at" to trialEndsAt,
        "product_id" to productId,
        "entitlement_ids" to entitlementIds,
        "store" to store,
        "period_type" to periodType,
        "expiration_at" to expirationAt,
        "grace_period_expiration_at" to gracePeriodExpirationAt,
    )
}
