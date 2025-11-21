package co.ke.tinker.config

class Configuration(
    val apiPublicKey: String,
    val apiSecretKey: String
) {
    val baseUrl: String = "${Endpoints.API_BASE_URL}/"
    
    val apiKey: String
        get() = apiSecretKey
}

