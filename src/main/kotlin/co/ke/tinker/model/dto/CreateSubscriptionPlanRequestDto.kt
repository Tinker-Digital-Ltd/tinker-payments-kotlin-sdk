package co.ke.tinker.model.dto

data class CreateSubscriptionPlanRequestDto(
    val name: String,
    val amount: Double,
    val currency: String,
    val intervals: List<String>,
    val description: String = "",
    val isActive: Boolean = true,
    val featuresJson: Map<String, Any?>? = null,
) {
    fun toMap(): Map<String, Any?> = mapOf<String, Any?>(
        "name" to name,
        "description" to description,
        "amount" to amount,
        "currency" to currency,
        "intervals" to intervals,
        "is_active" to isActive,
    ).let { if (featuresJson == null) it else it + ("features_json" to featuresJson) }
}
