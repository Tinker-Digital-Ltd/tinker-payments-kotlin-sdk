package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.config.Endpoints
import co.ke.tinker.http.HttpClient
import co.ke.tinker.model.Transaction
import co.ke.tinker.model.dto.InitiatePaymentRequestDto
import co.ke.tinker.model.dto.QueryPaymentRequestDto
import co.ke.tinker.model.lifecycle.Cancellation
import co.ke.tinker.model.lifecycle.CancellationHistory
import co.ke.tinker.model.lifecycle.CancellationRequest
import co.ke.tinker.model.lifecycle.CancellationResult
import co.ke.tinker.model.lifecycle.Refund
import co.ke.tinker.model.lifecycle.RefundHistory
import co.ke.tinker.model.lifecycle.RefundRequest
import co.ke.tinker.model.lifecycle.RefundResult

class TransactionManager(
    config: Configuration,
    httpClient: HttpClient,
    authManager: AuthenticationManager,
) : BaseManager(config, httpClient, authManager) {

    fun initiate(request: InitiatePaymentRequestDto): Transaction {
        val payload = request.toMap()
        val response = request("POST", Endpoints.PAYMENT_INITIATE_PATH, payload)
        return Transaction(response)
    }

    fun query(request: QueryPaymentRequestDto): Transaction {
        val payload = request.toMap()
        val response = request("POST", Endpoints.PAYMENT_QUERY_PATH, payload)
        return Transaction(response)
    }

    fun refund(paymentId: String, payload: RefundRequest, key: String): RefundResult {
        payload.amount?.let { LifecycleCodec.positiveAmount(it) }
        return LifecycleCodec.decode(request("POST", LifecycleCodec.payment(paymentId) + "/refund", LifecycleCodec.payload(payload), LifecycleCodec.headers(key)))
    }

    fun refunds(paymentId: String): RefundHistory =
        LifecycleCodec.decode(request("GET", LifecycleCodec.payment(paymentId) + "/refunds", null))

    fun getRefund(paymentId: String, refundId: String): Refund =
        LifecycleCodec.decode(request("GET", LifecycleCodec.payment(paymentId) + "/refunds/" + LifecycleCodec.encode(refundId), null))

    fun cancelCheckout(paymentId: String, payload: CancellationRequest, key: String): CancellationResult =
        LifecycleCodec.decode(request("POST", LifecycleCodec.payment(paymentId) + "/cancellations", LifecycleCodec.payload(payload), LifecycleCodec.headers(key)))

    fun cancellations(paymentId: String): CancellationHistory =
        LifecycleCodec.decode(request("GET", LifecycleCodec.payment(paymentId) + "/cancellations", null))

    fun getCancellation(paymentId: String, requestId: String): Cancellation =
        LifecycleCodec.decode(request("GET", LifecycleCodec.payment(paymentId) + "/cancellations/" + LifecycleCodec.encode(requestId), null))
}
