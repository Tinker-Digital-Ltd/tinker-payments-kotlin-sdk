package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PayoutResult(
    @JsonProperty("withdrawal") val withdrawal: Payout,
    @JsonProperty("replayed") val replayed: Boolean,
)
