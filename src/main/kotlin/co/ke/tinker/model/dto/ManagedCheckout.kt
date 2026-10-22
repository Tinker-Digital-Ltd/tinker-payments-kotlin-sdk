package co.ke.tinker.model.dto

/** Exact integer minor-unit prices for managed recurring billing. */
data class ManagedCheckout @JvmOverloads constructor(
    val externalCustomerId: String,
    val email: String,
    val name: String,
    val currency: String,
    val unitAmount: Long,
    val quantity: Int,
    val interval: String,
    val returnUrl: String,
    val idempotencyKey: String,
    val trialDays: Int = 0,
) {
    fun toMap(): Map<String, Any?> = buildMap {
        put("external_customer_id", externalCustomerId)
        put("email", email)
        put("name", name)
        put("currency", currency)
        put("unit_amount", unitAmount)
        put("quantity", quantity)
        put("interval", interval)
        put("return_url", returnUrl)
        put("idempotency_key", idempotencyKey)
        if (trialDays > 0) put("trial_days", trialDays)
    }
}
