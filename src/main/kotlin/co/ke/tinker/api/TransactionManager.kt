package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.config.Endpoints
import co.ke.tinker.http.HttpClient
import co.ke.tinker.model.Transaction
import co.ke.tinker.model.dto.InitiatePaymentRequestDto
import co.ke.tinker.model.dto.QueryPaymentRequestDto

class TransactionManager(
    config: Configuration,
    httpClient: HttpClient,
    authManager: AuthenticationManager
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
}

