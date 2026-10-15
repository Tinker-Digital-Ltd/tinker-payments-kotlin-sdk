package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonProperty

enum class PayoutStatus {
    @JsonProperty("pending_approval")
    PENDING_APPROVAL,

    @JsonProperty("approved")
    APPROVED,

    @JsonProperty("processing")
    PROCESSING,

    @JsonProperty("paid")
    PAID,

    @JsonProperty("rejected")
    REJECTED,
}
