package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Refund(
    @JsonProperty("id") val id: String,
    @JsonProperty("payment_id") val paymentId: String,
    @JsonProperty("amount") val amount: Double,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("reason") val reason: RefundReason,
    @JsonProperty("note") val note: String? = null,
    @JsonProperty("source") val source: String,
    @JsonProperty("requested_by_app_id") val requestedByAppId: String? = null,
    @JsonProperty("status") val status: RefundStatus,
    @JsonProperty("execution") val execution: String? = null,
    @JsonProperty("provider_refund_id") val providerRefundId: String? = null,
    @JsonProperty("manual_reference") val manualReference: String? = null,
    @JsonProperty("rejection_reason") val rejectionReason: String? = null,
    @JsonProperty("failure_message") val failureMessage: String? = null,
    @JsonProperty("reviewed_at") val reviewedAt: String? = null,
    @JsonProperty("completed_at") val completedAt: String? = null,
    @JsonProperty("created_at") val createdAt: String,
    @JsonProperty("updated_at") val updatedAt: String,
)
