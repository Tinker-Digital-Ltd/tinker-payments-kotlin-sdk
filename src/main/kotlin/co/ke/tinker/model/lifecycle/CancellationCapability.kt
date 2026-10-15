package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CancellationCapability(
    @JsonProperty("supported") val supported: Boolean,
    @JsonProperty("eligible") val eligible: Boolean,
    @JsonProperty("reason_code") val reasonCode: String,
)
