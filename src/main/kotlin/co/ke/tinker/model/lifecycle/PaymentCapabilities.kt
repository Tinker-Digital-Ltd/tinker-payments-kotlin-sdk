package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentCapabilities(
    @JsonProperty("providers") val providers: List<ProviderCapabilities>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProviderCapabilities(
    @JsonProperty("gateway") val gateway: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("methods") val methods: List<String>,
    @JsonProperty("currencies") val currencies: List<String>,
    @JsonProperty("countries") val countries: List<String>,
    @JsonProperty("capabilities") val capabilities: Map<String, String>,
)
