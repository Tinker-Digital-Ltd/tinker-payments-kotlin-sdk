package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PayoutFilters(
    @JsonProperty("page") val page: Int? = null,
    @JsonProperty("per_page") val perPage: Int? = null,
    @JsonProperty("status") val status: PayoutStatus? = null,
    @JsonProperty("currency") val currency: String? = null,
)
