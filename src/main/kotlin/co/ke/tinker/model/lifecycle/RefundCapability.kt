package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class RefundCapability(
    @JsonProperty("supported") val supported: Boolean,
    @JsonProperty("partial") val partial: Boolean,
    @JsonProperty("reasons") val reasons: List<String>,
    @JsonProperty("max_amount") val maxAmount: Double,
    @JsonProperty("review_required") val reviewRequired: Boolean,
    @JsonProperty("execution") val execution: String,
    @JsonProperty("unavailable_reason") val unavailableReason: String? = null,
)
