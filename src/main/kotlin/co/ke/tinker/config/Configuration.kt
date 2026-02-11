package co.ke.tinker.config

class Configuration(
    val apiPublicKey: String,
    val apiSecretKey: String,
    baseUrl: String? = null
) {
    val baseUrl: String
    val authUrl: String

    init {
        var resolvedBaseUrl = baseUrl
        if (resolvedBaseUrl.isNullOrBlank()) {
            resolvedBaseUrl = if (isSandboxCredentials()) Endpoints.SANDBOX_BASE_URL else Endpoints.PRODUCTION_BASE_URL
        }

        resolvedBaseUrl = resolvedBaseUrl.removeSuffix("/")
        if (!resolvedBaseUrl.endsWith(Endpoints.API_VERSION_PATH)) {
            resolvedBaseUrl += Endpoints.API_VERSION_PATH
        }

        this.baseUrl = "$resolvedBaseUrl/"
        this.authUrl = "$resolvedBaseUrl${Endpoints.AUTH_TOKEN_PATH}"
    }

    val apiKey: String
        get() = apiSecretKey

    private fun isSandboxCredentials(): Boolean {
        return apiPublicKey.startsWith("pk_test_") || apiSecretKey.startsWith("sk_test_")
    }
}
