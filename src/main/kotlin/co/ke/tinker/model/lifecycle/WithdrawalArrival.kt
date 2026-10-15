package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class WithdrawalArrival(
    @JsonProperty("min") val min: Int,
    @JsonProperty("max") val max: Int,
    @JsonProperty("unit") val unit: String,
)
