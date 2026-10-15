package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PayoutPage(
    @JsonProperty("payouts") val payouts: List<Payout>,
    @JsonProperty("pagination") val pagination: Pagination,
)
