package co.ke.tinker.model.dto

import co.ke.tinker.enums.PaymentStatus

data class InitiationDataDto(
    val paymentReference: String?,
    val status: PaymentStatus,
    val authorizationUrl: String?
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): InitiationDataDto {
            val paymentReference = data["payment_reference"] as? String
            val statusValue = data["status"] as? String ?: "pending"
            val status = PaymentStatus.fromString(statusValue)
            val authorizationUrl = data["authorization_url"] as? String
            return InitiationDataDto(paymentReference, status, authorizationUrl)
        }
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "payment_reference" to paymentReference,
            "status" to status.value,
            "authorization_url" to authorizationUrl
        )
    }
}

