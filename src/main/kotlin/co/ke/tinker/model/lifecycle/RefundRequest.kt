package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class RefundRequest(
    @JsonProperty("amount") val amount: Double? = null,
    @JsonProperty("reason") val reason: RefundReason,
    @JsonProperty("note") val note: String? = null,
)
