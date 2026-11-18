package co.ke.tinker.config

object Endpoints {
    const val PRODUCTION_BASE_URL = "https://api.tinkerpayments.com"
    const val SANDBOX_BASE_URL = "https://sandbox-api.tinkerpayments.com"
    const val API_VERSION_PATH = "/v1"
    const val AUTH_TOKEN_PATH = "/auth/token"
    const val PAYMENT_INITIATE_PATH = "/merchant/payment/initiate"
    const val PAYMENT_QUERY_PATH = "/merchant/payment/query"
    const val SUBSCRIPTION_BASE_PATH = "/merchant/subscriptions"
    const val SUBSCRIPTION_PLANS_PATH = "/merchant/subscriptions/plans"
    const val SUBSCRIPTION_INVOICES_PATH = "/merchant/subscriptions/invoices"
    const val SUBSCRIPTION_RENEWAL_PROMPTS_PATH = "/merchant/subscriptions/renewal_prompts"
    const val CUSTOMERS_PATH = "/merchant/customers"
    const val ENTITLEMENTS_PATH = "/merchant/entitlements"
    const val PRODUCTS_PATH = "/merchant/products"
    const val OFFERINGS_PATH = "/merchant/offerings"
    const val METRICS_PATH = "/merchant/metrics"
    const val EXPERIMENTS_PATH = "/merchant/experiments"
    const val INTEGRATIONS_PATH = "/merchant/integrations"
}
