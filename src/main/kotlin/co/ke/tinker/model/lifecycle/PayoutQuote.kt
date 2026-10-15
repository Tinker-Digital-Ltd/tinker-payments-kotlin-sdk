package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PayoutQuote(
    @JsonProperty("method_type") val methodType: String,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("amount") val amount: Double? = null,
    @JsonProperty("fee_amount") val feeAmount: Double,
    @JsonProperty("net_amount") val netAmount: Double? = null,
    @JsonProperty("fee") val fee: WithdrawalFeeRule,
    @JsonProperty("review_time") val reviewTime: String,
    @JsonProperty("arrival") val arrival: WithdrawalArrival? = null,
    @JsonProperty("arrival_text") val arrivalText: String,
    @JsonProperty("configured") val configured: Boolean,
    @JsonProperty("is_estimate") val isEstimate: Boolean,
)
