package co.ke.tinker.model.dto

data class SubscriptionCustomerDto(
    val externalCustomerId: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val metadata: Map<String, Any?>? = null
) {
    fun toMap(): MutableMap<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            "external_customer_id" to externalCustomerId,
            "name" to name
        )
        email?.let { map["email"] = it }
        phone?.let { map["phone"] = it }
        metadata?.let { map["metadata"] = it }
        return map
    }
}
