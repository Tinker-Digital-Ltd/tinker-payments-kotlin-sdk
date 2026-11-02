package co.ke.tinker.model.dto

import co.ke.tinker.enums.Gateway

data class InitiatePaymentRequestDto(
    val amount: Double,
    val currency: String,
    val gateway: Gateway,
    val merchantReference: String,
    val returnUrl: String,
    val customerPhone: String? = null,
    val customerEmail: String? = null,
    val transactionDesc: String? = null,
    val metadata: Map<String, Any?>? = null,
    val paymentMethod: String? = null,
) {
    fun toMap(): Map<String, Any?> {
        val payload = mutableMapOf<String, Any?>(
            "amount" to amount,
            "currency" to currency,
            "gateway" to gateway.value,
            "merchantReference" to merchantReference,
            "returnUrl" to returnUrl,
        )

        customerPhone?.let { payload["customerPhone"] = it }
        customerEmail?.let { payload["customerEmail"] = it }
        transactionDesc?.let { payload["transactionDesc"] = it }
        metadata?.let { payload["metadata"] = it }
        paymentMethod?.let { payload["paymentMethod"] = it }

        return payload
    }
}
