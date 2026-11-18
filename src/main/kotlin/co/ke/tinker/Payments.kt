package co.ke.tinker

import co.ke.tinker.api.CustomerManager
import co.ke.tinker.api.EntitlementManager
import co.ke.tinker.api.ExperimentManager
import co.ke.tinker.api.IntegrationManager
import co.ke.tinker.api.MetricsManager
import co.ke.tinker.api.OfferingManager
import co.ke.tinker.api.PayoutManager
import co.ke.tinker.api.ProductManager
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
    baseUrl: String? = null,
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

    private val payoutManager: PayoutManager by lazy {
        PayoutManager(config, this.httpClient, authManager)
    }

    private val customerManager: CustomerManager by lazy {
        CustomerManager(config, this.httpClient, authManager)
    }

    private val entitlementManager: EntitlementManager by lazy {
        EntitlementManager(config, this.httpClient, authManager)
    }

    private val productManager: ProductManager by lazy {
        ProductManager(config, this.httpClient, authManager)
    }

    private val offeringManager: OfferingManager by lazy {
        OfferingManager(config, this.httpClient, authManager)
    }

    private val metricsManager: MetricsManager by lazy {
        MetricsManager(config, this.httpClient, authManager)
    }

    private val experimentManager: ExperimentManager by lazy {
        ExperimentManager(config, this.httpClient, authManager)
    }

    private val integrationManager: IntegrationManager by lazy {
        IntegrationManager(config, this.httpClient, authManager)
    }

    private val webhookHandler: WebhookHandler by lazy {
        WebhookHandler()
    }

    fun transactions(): TransactionManager = transactionsManager
    fun subscriptions(): SubscriptionManager = subscriptionsManager
    fun payouts(): PayoutManager = payoutManager
    fun customers(): CustomerManager = customerManager
    fun entitlements(): EntitlementManager = entitlementManager
    fun products(): ProductManager = productManager
    fun offerings(): OfferingManager = offeringManager
    fun metrics(): MetricsManager = metricsManager
    fun experiments(): ExperimentManager = experimentManager
    fun integrations(): IntegrationManager = integrationManager
    fun webhooks(): WebhookHandler = webhookHandler
    fun getLastAuthMeta(): ApiMeta? = authManager.lastMeta
}
