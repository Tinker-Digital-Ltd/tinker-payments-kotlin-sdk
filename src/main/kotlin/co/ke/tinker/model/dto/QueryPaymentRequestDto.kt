package co.ke.tinker.model.dto

import co.ke.tinker.enums.Gateway

data class QueryPaymentRequestDto(
    val paymentReference: String,
    val gatewayId: String,
) {
    constructor(paymentReference: String, gateway: Gateway) : this(paymentReference, gateway.value)

    val gateway: Gateway? get() = Gateway.entries.firstOrNull { it.value == gatewayId }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "reference" to paymentReference,
            "gateway" to gatewayId,
        )
    }
}
