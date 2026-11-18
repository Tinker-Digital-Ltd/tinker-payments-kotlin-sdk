package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.config.Endpoints
import co.ke.tinker.http.HttpClient

class MetricsManager(
    config: Configuration,
    httpClient: HttpClient,
    authManager: AuthenticationManager,
) : BaseManager(config, httpClient, authManager) {

    fun get(from: String? = null, to: String? = null): Map<String, Any?> =
        request("GET", withQuery(Endpoints.METRICS_PATH, "from" to from, "to" to to), null)
}
