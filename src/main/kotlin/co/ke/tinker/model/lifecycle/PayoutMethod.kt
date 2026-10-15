package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PayoutMethod(
    @JsonProperty("id") val id: String,
    @JsonProperty("method_type") val methodType: String,
    @JsonProperty("provider") val provider: String? = null,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("status") val status: String,
    @JsonProperty("is_verified") val isVerified: Boolean,
    @JsonProperty("is_default") val isDefault: Boolean,
    @JsonProperty("masked_destination") val maskedDestination: String,
)
