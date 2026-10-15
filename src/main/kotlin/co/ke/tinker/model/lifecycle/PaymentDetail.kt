package co.ke.tinker.model.lifecycle

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaymentDetail(
    @JsonProperty("id") val id: String,
    @JsonProperty("status") val status: String,
    @JsonProperty("amount") val amount: Double,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("refundable_amount") val refundableAmount: Double? = null,
    @JsonProperty("refund_capability") val refundCapability: RefundCapability? = null,
    @JsonProperty("refunds") val refunds: List<Refund>? = null,
)
