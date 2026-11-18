package co.ke.tinker.webhook.dto

data class CustomerEventDataDto(
    val transferredFrom: List<String>,
    val transferredTo: List<String>,
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): CustomerEventDataDto = CustomerEventDataDto(
            (data["transferred_from"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            (data["transferred_to"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        )
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "transferred_from" to transferredFrom,
        "transferred_to" to transferredTo,
    )
}
