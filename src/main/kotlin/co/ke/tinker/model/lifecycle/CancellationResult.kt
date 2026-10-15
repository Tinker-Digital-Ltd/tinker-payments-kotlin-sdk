package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CancellationResult(
    @JsonProperty("cancellation") val cancellation: Cancellation,
    @JsonProperty("replayed") val replayed: Boolean,
)
