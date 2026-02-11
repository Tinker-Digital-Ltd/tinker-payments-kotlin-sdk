package co.ke.tinker.model.dto

data class CreateSubscriptionPlanRequestDto(
    val name: String,
    val amount: Double,
    val currency: String,
    val intervals: List<String>,
    val description: String = "",
    val isActive: Boolean = true
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "description" to description,
        "amount" to amount,
        "currency" to currency,
        "intervals" to intervals,
        "is_active" to isActive
    )
}
