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
    val customerName: String? = null,
    val country: String? = null,
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
        customerName: String? = null,
        country: String? = null,
    ) : this(amount, currency, gateway.value, merchantReference, returnUrl, customerPhone, customerEmail, transactionDesc, metadata, paymentMethod, customerName, country)

    val gateway: String get() = gatewayId

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
        customerName?.let { payload["customerName"] = it }
        country?.let { payload["country"] = it }
        transactionDesc?.let { payload["transactionDesc"] = it }
        metadata?.let { payload["metadata"] = it }
        paymentMethod?.let { payload["paymentMethod"] = it }

        return payload
    }
}
