package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonProperty

enum class CancellationStatus {
    @JsonProperty("requested")
    REQUESTED,

    @JsonProperty("processing")
    PROCESSING,

    @JsonProperty("cancelled")
    CANCELLED,

    @JsonProperty("rejected")
    REJECTED,

    @JsonProperty("outcome_unknown")
    OUTCOME_UNKNOWN,
}
