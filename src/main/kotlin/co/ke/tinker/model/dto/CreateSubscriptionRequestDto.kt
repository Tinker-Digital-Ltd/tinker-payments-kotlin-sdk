package co.ke.tinker.model.dto

data class CreateSubscriptionRequestDto(
    val planId: String,
    val gateway: String,
    val customer: SubscriptionCustomerDto,
    val paymentMethodId: String? = null,
    val billingPeriod: String? = null
) {
    fun toMap(): MutableMap<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            "plan_id" to planId,
            "gateway" to gateway,
            "customer" to customer.toMap()
        )
        paymentMethodId?.let { map["payment_method_id"] = it }
        billingPeriod?.let { map["billing_period"] = it }
        return map
    }
}
