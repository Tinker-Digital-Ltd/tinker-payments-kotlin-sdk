package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Cancellation(
    @JsonProperty("id") val id: String,
    @JsonProperty("payment_id") val paymentId: String,
    @JsonProperty("status") val status: CancellationStatus,
    @JsonProperty("reason") val reason: String,
    @JsonProperty("reason_code") val reasonCode: String,
    @JsonProperty("source") val source: String,
    @JsonProperty("requested_by_app_id") val requestedByAppId: String? = null,
    @JsonProperty("created_at") val createdAt: String,
    @JsonProperty("updated_at") val updatedAt: String,
    @JsonProperty("completed_at") val completedAt: String? = null,
    @JsonProperty("failure_message") val failureMessage: String? = null,
)
