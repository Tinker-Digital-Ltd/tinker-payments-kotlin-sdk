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
}
