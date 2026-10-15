package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class WithdrawalFeeRule(
    @JsonProperty("fixed") val fixed: Double,
    @JsonProperty("percent") val percent: Double,
    @JsonProperty("min") val min: Double,
    @JsonProperty("max") val max: Double,
)
