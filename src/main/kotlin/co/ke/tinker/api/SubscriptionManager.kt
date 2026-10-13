package co.ke.tinker.api

import co.ke.tinker.auth.AuthenticationManager
import co.ke.tinker.config.Configuration
import co.ke.tinker.config.Endpoints
import co.ke.tinker.http.HttpClient
import co.ke.tinker.model.dto.CreateSubscriptionPlanRequestDto
import co.ke.tinker.model.dto.CreateSubscriptionRequestDto
import co.ke.tinker.model.dto.ManagedCheckout
import java.net.URLEncoder

class SubscriptionManager(
    config: Configuration,
    httpClient: HttpClient,
    authManager: AuthenticationManager,
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
        return request("POST", "${Endpoints.SUBSCRIPTION_BASE_PATH}/$subscriptionId/cancel", null)
    }
    fun checkout(input: ManagedCheckout): Map<String, Any?> {
        return request("POST", "merchant/billing/checkout", input.toMap())
    }

    fun billingResource(id: String): Map<String, Any?> {
        return request("GET", billingResourcePath(id), null)
    }

    fun changeQuantity(id: String, quantity: Int, key: String): Map<String, Any?> {
        return request("POST", "${billingResourcePath(id)}/quantity", mapOf("quantity" to quantity, "idempotency_key" to key))
    }

    fun scheduleQuantity(id: String, quantity: Int, key: String): Map<String, Any?> {
        return request("POST", "${billingResourcePath(id)}/schedule", mapOf("quantity" to quantity, "idempotency_key" to key))
    }

    fun clearScheduledQuantity(id: String, key: String): Map<String, Any?> {
        return request("POST", "${billingResourcePath(id)}/unschedule", mapOf("idempotency_key" to key))
    }

    fun setCancelAtRenewal(id: String, cancel: Boolean, key: String): Map<String, Any?> {
        return request("POST", "${billingResourcePath(id)}/cancel", mapOf("cancel" to cancel, "idempotency_key" to key))
    }

    fun billingOperation(key: String): Map<String, Any?> {
        return request("GET", "merchant/billing/operations/${escapePath(key)}", null)
    }

    fun paymentMethodPortal(id: String, returnUrl: String, key: String): Map<String, Any?> {
        return request("POST", "${billingResourcePath(id)}/payment-method", mapOf("return_url" to returnUrl, "idempotency_key" to key))
    }

    fun billingPaymentHistory(id: String, cursor: String = "", snapshot: Long = 0): Map<String, Any?> {
        val query = mutableListOf<String>()
        if (cursor.isNotEmpty()) query.add("starting_after=${escapePath(cursor)}")
        if (snapshot > 0) query.add("snapshot=$snapshot")
        val endpoint = "${billingResourcePath(id)}/payment-history" +
            if (query.isEmpty()) "" else "?${query.joinToString("&")}"
        return request("GET", endpoint, null)
    }

    private fun billingResourcePath(id: String): String = "merchant/billing/resources/${escapePath(id)}"

    private fun escapePath(value: String): String = URLEncoder.encode(value, Charsets.UTF_8.name()).replace("+", "%20")
}
