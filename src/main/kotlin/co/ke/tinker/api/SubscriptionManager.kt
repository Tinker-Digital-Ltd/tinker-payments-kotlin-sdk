package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.config.Endpoints
import co.ke.tinker.http.HttpClient
import co.ke.tinker.model.dto.CreateSubscriptionPlanRequestDto
import co.ke.tinker.model.dto.CreateSubscriptionRequestDto
import java.net.URLEncoder

class SubscriptionManager(
    config: Configuration,
    httpClient: HttpClient,
    authManager: AuthenticationManager
) : BaseManager(config, httpClient, authManager) {

    fun createPlan(request: CreateSubscriptionPlanRequestDto): Map<String, Any?> {
        return request("POST", Endpoints.SUBSCRIPTION_PLANS_PATH, request.toMap())
    }

    @Suppress("UNCHECKED_CAST")
    fun listPlans(): List<Map<String, Any?>> {
        val wrapped = request("GET", Endpoints.SUBSCRIPTION_PLANS_PATH, null)
        return wrapped["value"] as? List<Map<String, Any?>> ?: emptyList()
    }

    fun create(request: CreateSubscriptionRequestDto): Map<String, Any?> {
        return request("POST", Endpoints.SUBSCRIPTION_BASE_PATH, request.toMap())
    }

    @Suppress("UNCHECKED_CAST")
    fun list(planId: String? = null, externalCustomerId: String? = null): List<Map<String, Any?>> {
        val query = mutableListOf<String>()
        if (!planId.isNullOrBlank()) {
            query.add("plan_id=${URLEncoder.encode(planId, Charsets.UTF_8.name())}")
        }
        if (!externalCustomerId.isNullOrBlank()) {
            query.add("external_customer_id=${URLEncoder.encode(externalCustomerId, Charsets.UTF_8.name())}")
        }

        var endpoint = Endpoints.SUBSCRIPTION_BASE_PATH
        if (query.isNotEmpty()) {
            endpoint += "?${query.joinToString("&")}" 
        }

        val wrapped = request("GET", endpoint, null)
        return wrapped["value"] as? List<Map<String, Any?>> ?: emptyList()
    }

    fun cancel(subscriptionId: String): Map<String, Any?> {
        return request("POST", "${Endpoints.SUBSCRIPTION_BASE_PATH}/${subscriptionId}/cancel", null)
    }
}
