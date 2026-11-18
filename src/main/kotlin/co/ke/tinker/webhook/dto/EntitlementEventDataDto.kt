package co.ke.tinker.webhook.dto

data class EntitlementEventDataDto(
    val id: String?,
    val transactionId: String?,
    val productId: String?,
    val externalCustomerId: String?,
    val entitlementIds: List<String>,
    val store: String?,
    val periodType: String?,
    val amount: Double?,
    val currency: String?,
    val purchasedAt: String?,
    val expirationAt: String?,
    val presentedOfferingId: String?,
    val revokedAt: String?,
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): EntitlementEventDataDto = EntitlementEventDataDto(
            data["id"] as? String,
            data["transaction_id"] as? String,
            data["product_id"] as? String,
            data["external_customer_id"] as? String,
            (data["entitlement_ids"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            data["store"] as? String,
            data["period_type"] as? String,
            (data["amount"] as? Number)?.toDouble(),
            data["currency"] as? String,
            data["purchased_at"] as? String,
            data["expiration_at"] as? String,
            data["presented_offering_id"] as? String,
            data["revoked_at"] as? String,
        )
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "transaction_id" to transactionId,
        "product_id" to productId,
        "external_customer_id" to externalCustomerId,
        "entitlement_ids" to entitlementIds,
        "store" to store,
        "period_type" to periodType,
        "amount" to amount,
        "currency" to currency,
        "purchased_at" to purchasedAt,
        "expiration_at" to expirationAt,
        "presented_offering_id" to presentedOfferingId,
        "revoked_at" to revokedAt,
    )
}
