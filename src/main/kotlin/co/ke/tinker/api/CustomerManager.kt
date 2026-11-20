package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.config.Endpoints
import co.ke.tinker.http.HttpClient

class CustomerManager(
    config: Configuration,
    httpClient: HttpClient,
    authManager: AuthenticationManager,
) : BaseManager(config, httpClient, authManager) {

    fun listCustomers(search: String? = null, page: Int? = null, pageSize: Int? = null): Map<String, Any?> =
        request("GET", withQuery(Endpoints.CUSTOMERS_PATH, "search" to search, "page" to page, "page_size" to pageSize), null)

    fun getCustomer(externalCustomerId: String): Map<String, Any?> = request("GET", path(externalCustomerId), null)

    fun getEntitlement(externalCustomerId: String, identifier: String): Map<String, Any?> =
        request("GET", entitlementPath(externalCustomerId, identifier), null)

    fun grantPromotionalEntitlement(
        externalCustomerId: String,
        identifier: String,
        duration: String? = null,
        endTime: String? = null,
    ): Map<String, Any?> {
        val body = mutableMapOf<String, Any?>()
        duration?.let { body["duration"] = it }
        endTime?.let { body["end_time"] = it }
        return request("POST", "${entitlementPath(externalCustomerId, identifier)}/promotional", body)
    }

    fun revokePromotionalEntitlements(externalCustomerId: String, identifier: String): Map<String, Any?> =
        request("POST", "${entitlementPath(externalCustomerId, identifier)}/revoke_promotionals", null)

    fun alias(externalCustomerId: String, alias: String): Map<String, Any?> =
        request("POST", "${path(externalCustomerId)}/alias", mapOf("alias" to alias))

    fun transfer(externalCustomerId: String, to: String): Map<String, Any?> =
        request("POST", "${path(externalCustomerId)}/transfer", mapOf("to" to to))

    fun setAttributes(externalCustomerId: String, attributes: Map<String, String?>): Map<String, Any?> =
        request("POST", "${path(externalCustomerId)}/attributes", mapOf("attributes" to attributes))

    fun getOfferings(externalCustomerId: String): Map<String, Any?> =
        request("GET", "${path(externalCustomerId)}/offerings", null)

    fun createToken(externalCustomerId: String, expiresIn: Int? = null): Map<String, Any?> =
        request("POST", "${path(externalCustomerId)}/token", expiresIn?.let { mapOf("expires_in" to it) })

    private fun path(externalCustomerId: String): String = "${Endpoints.CUSTOMERS_PATH}/${escapePath(externalCustomerId)}"

    private fun entitlementPath(externalCustomerId: String, identifier: String): String =
        "${path(externalCustomerId)}/entitlements/${escapePath(identifier)}"
}
