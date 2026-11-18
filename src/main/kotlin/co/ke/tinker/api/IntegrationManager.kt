package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.config.Endpoints
import co.ke.tinker.http.HttpClient

class IntegrationManager(
    config: Configuration,
    httpClient: HttpClient,
    authManager: AuthenticationManager,
) : BaseManager(config, httpClient, authManager) {

    fun list(): List<Map<String, Any?>> = requestList(Endpoints.INTEGRATIONS_PATH)

    fun put(kind: String, request: Map<String, Any?>): Map<String, Any?> = request("PUT", path(kind), request)

    fun delete(kind: String): Map<String, Any?> = request("DELETE", path(kind), null)

    fun test(kind: String): Map<String, Any?> = request("POST", "${path(kind)}/test", null)

    private fun path(kind: String): String = "${Endpoints.INTEGRATIONS_PATH}/${escapePath(kind)}"
}
