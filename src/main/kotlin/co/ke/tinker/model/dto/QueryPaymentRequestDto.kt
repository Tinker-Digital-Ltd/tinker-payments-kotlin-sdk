package co.ke.tinker.model.dto

import co.ke.tinker.enums.Gateway

data class QueryPaymentRequestDto(
    val paymentReference: String,
    val gateway: Gateway
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "payment_reference" to paymentReference,
            "gateway" to gateway.value
        )
    }
}

