package co.ke.tinker.model.dto

import co.ke.tinker.enums.PaymentStatus

data class InitiationDataDto(
    val paymentReference: String?,
    val status: PaymentStatus,
    val authorizationUrl: String?,
    val clientSecret: String? = null,
    val publishableKey: String? = null,
    val rawStatus: String = status.value,
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): InitiationDataDto {
            val paymentReference = (data["paymentReference"] ?: data["payment_reference"]) as? String
            val statusValue = PaymentStatus.normalize(data["status"] as? String)
            val status = PaymentStatus.fromString(statusValue)
            val authorizationUrl = (data["authorizationUrl"] ?: data["authorization_url"]) as? String
            val clientSecret = (data["clientSecret"] ?: data["client_secret"]) as? String
            val publishableKey = (data["publishableKey"] ?: data["publishable_key"]) as? String
            return InitiationDataDto(paymentReference, status, authorizationUrl, clientSecret, publishableKey, statusValue)
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "paymentReference" to paymentReference,
            "status" to rawStatus,
            "authorizationUrl" to authorizationUrl,
            "clientSecret" to clientSecret,
            "publishableKey" to publishableKey,
        )
    }
}
