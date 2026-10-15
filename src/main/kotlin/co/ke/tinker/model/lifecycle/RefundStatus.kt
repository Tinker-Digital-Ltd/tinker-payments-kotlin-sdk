package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonProperty

enum class RefundStatus {
    @JsonProperty("pending_review")
    PENDING_REVIEW,

    @JsonProperty("processing")
    PROCESSING,

    @JsonProperty("approved")
    APPROVED,

    @JsonProperty("failed")
    FAILED,

    @JsonProperty("refunded")
    REFUNDED,

    @JsonProperty("rejected")
    REJECTED,
}
