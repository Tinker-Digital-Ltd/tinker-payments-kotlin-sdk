package co.ke.tinker

import co.ke.tinker.api.TransactionManager
import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.http.HttpClient
import co.ke.tinker.webhook.WebhookHandler

class Payments(
    apiPublicKey: String,
    apiSecretKey: String,
    httpClient: HttpClient? = null
) {
    private val config = Configuration(apiPublicKey, apiSecretKey)
    private val httpClient: HttpClient = httpClient ?: HttpClient()
    private val authManager = AuthenticationManager(config, this.httpClient)
    
    private val transactionsManager: TransactionManager by lazy {
        TransactionManager(config, this.httpClient, authManager)
    }
    
    private val webhookHandler: WebhookHandler by lazy {
        WebhookHandler()
    }
    
    fun transactions(): TransactionManager = transactionsManager
    
    fun webhooks(): WebhookHandler = webhookHandler
}

