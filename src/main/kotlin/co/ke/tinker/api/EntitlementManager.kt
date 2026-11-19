package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.config.Endpoints
import co.ke.tinker.http.HttpClient

class EntitlementManager(
    config: Configuration,
    httpClient: HttpClient,
    authManager: AuthenticationManager,
) : BaseManager(config, httpClient, authManager) {

    fun list(): List<Map<String, Any?>> = requestList(Endpoints.ENTITLEMENTS_PATH)

    fun create(request: Map<String, Any?>): Map<String, Any?> = request("POST", Endpoints.ENTITLEMENTS_PATH, request)

    fun get(id: String): Map<String, Any?> = request("GET", path(id), null)

    fun update(id: String, displayName: String): Map<String, Any?> =
        request("PATCH", path(id), mapOf("display_name" to displayName))

    fun delete(id: String): Map<String, Any?> = request("DELETE", path(id), null)

    fun attachProducts(id: String, productIds: List<String>): Map<String, Any?> =
        request("POST", "${path(id)}/attach_products", mapOf("product_ids" to productIds))

    fun detachProducts(id: String, productIds: List<String>): Map<String, Any?> =
        request("POST", "${path(id)}/detach_products", mapOf("product_ids" to productIds))

    private fun path(id: String): String = "${Endpoints.ENTITLEMENTS_PATH}/${escapePath(id)}"
}
