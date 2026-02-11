package co.ke.tinker

import co.ke.tinker.api.SubscriptionManager
import co.ke.tinker.api.TransactionManager
import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.http.HttpClient
import co.ke.tinker.model.ApiMeta
import co.ke.tinker.webhook.WebhookHandler

class Payments(
    apiPublicKey: String,
    apiSecretKey: String,
    httpClient: HttpClient? = null,
    baseUrl: String? = null
) {
    private val config = Configuration(apiPublicKey, apiSecretKey, baseUrl)
    private val httpClient: HttpClient = httpClient ?: HttpClient()
    private val authManager = AuthenticationManager(config, this.httpClient)

    private val transactionsManager: TransactionManager by lazy {
        TransactionManager(config, this.httpClient, authManager)
    }

    private val subscriptionsManager: SubscriptionManager by lazy {
        SubscriptionManager(config, this.httpClient, authManager)
    }

    private val webhookHandler: WebhookHandler by lazy {
        WebhookHandler()
    }

    fun transactions(): TransactionManager = transactionsManager
    fun subscriptions(): SubscriptionManager = subscriptionsManager
    fun webhooks(): WebhookHandler = webhookHandler
    fun getLastAuthMeta(): ApiMeta? = authManager.lastMeta
}
