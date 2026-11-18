package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.config.Endpoints
import co.ke.tinker.http.HttpClient

class ExperimentManager(
    config: Configuration,
    httpClient: HttpClient,
    authManager: AuthenticationManager,
) : BaseManager(config, httpClient, authManager) {

    fun list(): List<Map<String, Any?>> = requestList(Endpoints.EXPERIMENTS_PATH)

    fun create(request: Map<String, Any?>): Map<String, Any?> = request("POST", Endpoints.EXPERIMENTS_PATH, request)

    fun get(id: String): Map<String, Any?> = request("GET", path(id), null)

    fun stop(id: String): Map<String, Any?> = request("POST", "${path(id)}/stop", null)

    private fun path(id: String): String = "${Endpoints.EXPERIMENTS_PATH}/${escapePath(id)}"
}
