package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CancellationHistory(
    @JsonProperty("cancellations") val cancellations: List<Cancellation>,
    @JsonProperty("capability") val capability: CancellationCapability,
    @JsonProperty("has_more") val hasMore: Boolean,
)
