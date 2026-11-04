package co.ke.tinker.model.dto

import co.ke.tinker.enums.Gateway

data class InitiatePaymentRequestDto(
    val amount: Double,
    val currency: String,
    val gatewayId: String,
    val merchantReference: String,
    val returnUrl: String,
    val customerPhone: String? = null,
    val customerEmail: String? = null,
    val transactionDesc: String? = null,
    val metadata: Map<String, Any?>? = null,
    val paymentMethod: String? = null,
) {
    constructor(
        amount: Double,
        currency: String,
        gateway: Gateway,
        merchantReference: String,
        returnUrl: String,
        customerPhone: String? = null,
        customerEmail: String? = null,
        transactionDesc: String? = null,
        metadata: Map<String, Any?>? = null,
        paymentMethod: String? = null,
    ) : this(amount, currency, gateway.value, merchantReference, returnUrl, customerPhone, customerEmail, transactionDesc, metadata, paymentMethod)

    val gateway: Gateway? get() = Gateway.entries.firstOrNull { it.value == gatewayId }

    fun toMap(): Map<String, Any?> {
        val payload = mutableMapOf<String, Any?>(
            "amount" to amount,
            "currency" to currency,
            "gateway" to gatewayId,
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
