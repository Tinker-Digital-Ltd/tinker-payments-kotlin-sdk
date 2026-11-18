package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.config.Endpoints
import co.ke.tinker.http.HttpClient

class OfferingManager(
    config: Configuration,
    httpClient: HttpClient,
    authManager: AuthenticationManager,
) : BaseManager(config, httpClient, authManager) {

    fun list(): List<Map<String, Any?>> = requestList(Endpoints.OFFERINGS_PATH)

    fun create(request: Map<String, Any?>): Map<String, Any?> = request("POST", Endpoints.OFFERINGS_PATH, request)

    fun get(id: String): Map<String, Any?> = request("GET", path(id), null)

    fun update(id: String, request: Map<String, Any?>): Map<String, Any?> = request("PATCH", path(id), request)

    fun delete(id: String): Map<String, Any?> = request("DELETE", path(id), null)

    private fun path(id: String): String = "${Endpoints.OFFERINGS_PATH}/${escapePath(id)}"
}
