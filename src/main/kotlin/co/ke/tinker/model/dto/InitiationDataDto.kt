package co.ke.tinker.model.dto

import co.ke.tinker.enums.PaymentStatus

data class InitiationDataDto(
    val paymentReference: String?,
    val status: PaymentStatus,
    val authorizationUrl: String?
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): InitiationDataDto {
            val paymentReference = (data["paymentReference"] ?: data["payment_reference"]) as? String
            val statusValue = data["status"] as? String ?: "pending"
            val status = PaymentStatus.fromString(statusValue)
            val authorizationUrl = (data["authorizationUrl"] ?: data["authorization_url"]) as? String
            return InitiationDataDto(paymentReference, status, authorizationUrl)
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "paymentReference" to paymentReference,
            "status" to status.value,
            "authorizationUrl" to authorizationUrl
        )
    }
}
