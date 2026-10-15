package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.http.HttpClient
import co.ke.tinker.model.lifecycle.Payout
import co.ke.tinker.model.lifecycle.PayoutFilters
import co.ke.tinker.model.lifecycle.PayoutMethod
import co.ke.tinker.model.lifecycle.PayoutPage
import co.ke.tinker.model.lifecycle.PayoutQuote
import co.ke.tinker.model.lifecycle.PayoutQuoteRequest
import co.ke.tinker.model.lifecycle.PayoutRequest
import co.ke.tinker.model.lifecycle.PayoutResult
import com.fasterxml.jackson.module.kotlin.convertValue

/** Merchant wallet withdrawals to its own verified destinations. */
class PayoutManager(config: Configuration, httpClient: HttpClient, authManager: AuthenticationManager) :
    BaseManager(config, httpClient, authManager) {

    fun methods(): List<PayoutMethod> =
        LifecycleCodec.mapper.convertValue(requireNotNull(request("GET", "merchant/payout-methods", null)["value"]))

    fun quote(payload: PayoutQuoteRequest): PayoutQuote {
        LifecycleCodec.positiveAmount(payload.amount)
        return LifecycleCodec.decode(request("GET", "merchant/payouts/quote" + LifecycleCodec.query(payload), null))
    }

    fun requestPayout(payload: PayoutRequest, key: String): PayoutResult {
        LifecycleCodec.positiveAmount(payload.amount)
        require(payload.expectedFeeAmount.isFinite() && payload.expectedFeeAmount >= 0) {
            "expected_fee_amount must be finite and non-negative"
        }
        return LifecycleCodec.decode(request("POST", "merchant/payouts", LifecycleCodec.payload(payload), LifecycleCodec.headers(key)))
    }

    fun list(filters: PayoutFilters = PayoutFilters()): PayoutPage =
        LifecycleCodec.decode(request("GET", "merchant/payouts" + LifecycleCodec.query(filters), null))

    fun get(id: String): Payout =
        LifecycleCodec.decode(request("GET", "merchant/payouts/" + LifecycleCodec.encode(id), null))
}
