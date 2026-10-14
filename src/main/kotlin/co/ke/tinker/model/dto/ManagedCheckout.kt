package co.ke.tinker.model.dto

/** Exact integer minor-unit prices for managed recurring billing. */
data class ManagedCheckout(
    val externalCustomerId: String,
    val email: String,
    val name: String,
    val currency: String,
    val unitAmount: Long,
    val quantity: Int,
    val interval: String,
    val returnUrl: String,
    val idempotencyKey: String,
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "external_customer_id" to externalCustomerId,
        "email" to email,
        "name" to name,
        "currency" to currency,
        "unit_amount" to unitAmount,
        "quantity" to quantity,
        "interval" to interval,
        "return_url" to returnUrl,
        "idempotency_key" to idempotencyKey,
    )
}
