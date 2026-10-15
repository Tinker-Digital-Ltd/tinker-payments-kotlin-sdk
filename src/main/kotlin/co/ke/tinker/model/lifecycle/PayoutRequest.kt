package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PayoutRequest(
    @JsonProperty("payout_method_id") val payoutMethodId: String,
    @JsonProperty("amount") val amount: Double,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("expected_fee_amount") val expectedFeeAmount: Double,
)
