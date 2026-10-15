package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Payout(
    @JsonProperty("id") val id: String,
    @JsonProperty("account_id") val accountId: String,
    @JsonProperty("payout_method_id") val payoutMethodId: String,
    @JsonProperty("amount") val amount: Double,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("fee_amount") val feeAmount: Double,
    @JsonProperty("net_amount") val netAmount: Double,
    @JsonProperty("status") val status: PayoutStatus,
    @JsonProperty("source") val source: String? = null,
    @JsonProperty("requested_by_app_id") val requestedByAppId: String? = null,
    @JsonProperty("requested_by_user_id") val requestedByUserId: String? = null,
    @JsonProperty("rejection_reason") val rejectionReason: String? = null,
    @JsonProperty("requested_at") val requestedAt: String,
    @JsonProperty("approved_at") val approvedAt: String? = null,
    @JsonProperty("processing_at") val processingAt: String? = null,
    @JsonProperty("paid_at") val paidAt: String? = null,
    @JsonProperty("rejected_at") val rejectedAt: String? = null,
    @JsonProperty("created_at") val createdAt: String,
    @JsonProperty("updated_at") val updatedAt: String,
)
