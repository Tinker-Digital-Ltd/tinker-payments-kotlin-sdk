package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PayoutQuoteRequest(
    @JsonProperty("payout_method_id") val payoutMethodId: String,
    @JsonProperty("amount") val amount: Double,
    @JsonProperty("currency") val currency: String? = null,
)
