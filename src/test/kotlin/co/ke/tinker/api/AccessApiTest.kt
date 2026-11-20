package co.ke.tinker.api

import co.ke.tinker.Payments
import co.ke.tinker.model.dto.InitiatePaymentRequestDto
import co.ke.tinker.webhook.WebhookEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.net.httpserver.HttpServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress

class AccessApiTest {
    private lateinit var server: HttpServer
    private lateinit var client: Payments
    private val mapper = ObjectMapper()
    private val calls = mutableListOf<List<Any?>>()
    private var responseBody = """{"success":true,"data":{"ok":true}}"""

    @BeforeEach
    fun setUp() {
        server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            val body: String
            if (exchange.requestURI.path.endsWith("/auth/token")) {
                body = """{"token":"token","expires_in":3600}"""
            } else {
                val requestBody = exchange.requestBody.readBytes().toString(Charsets.UTF_8)
                calls.add(
                    listOf(
                        exchange.requestMethod,
                        exchange.requestURI.toString().substring(4),
                        if (requestBody.isEmpty()) null else mapper.readValue(requestBody, Map::class.java),
                    ),
                )
                assertEquals("Bearer token", exchange.requestHeaders.getFirst("Authorization"))
                body = responseBody
            }
            val bytes = body.toByteArray(Charsets.UTF_8)
            exchange.sendResponseHeaders(200, bytes.size.toLong())
            exchange.responseBody.write(bytes)
            exchange.close()
        }
        server.start()
        client = Payments("public", "secret", null, "http://127.0.0.1:${server.address.port}")
    }

    @AfterEach
    fun tearDown() {
        if (::server.isInitialized) server.stop(0)
    }

    private fun expect(method: String, path: String, body: Map<String, Any?>? = null) {
        assertEquals(listOf(method, path, body), calls.last())
    }

    @Test
    fun customers() {
        client.customers().listCustomers("jo hn", 2, 50)
        expect("GET", "merchant/customers?search=jo+hn&page=2&page_size=50")
        client.customers().listCustomers()
        expect("GET", "merchant/customers")
        client.customers().getCustomer("cus/1")
        expect("GET", "merchant/customers/cus%2F1")
        client.customers().getEntitlement("c1", "pro")
        expect("GET", "merchant/customers/c1/entitlements/pro")
        client.customers().grantPromotionalEntitlement("c1", "pro", duration = "P7D")
        expect("POST", "merchant/customers/c1/entitlements/pro/promotional", mapOf("duration" to "P7D"))
        client.customers().revokePromotionalEntitlements("c1", "pro")
        expect("POST", "merchant/customers/c1/entitlements/pro/revoke_promotionals")
        client.customers().alias("c1", "\$anonymous:9")
        expect("POST", "merchant/customers/c1/alias", mapOf("alias" to "\$anonymous:9"))
        client.customers().transfer("c1", "c2")
        expect("POST", "merchant/customers/c1/transfer", mapOf("to" to "c2"))
        client.customers().setAttributes("c1", mapOf("plan" to "gold"))
        expect("POST", "merchant/customers/c1/attributes", mapOf("attributes" to mapOf("plan" to "gold")))
        client.customers().getOfferings("c1")
        expect("GET", "merchant/customers/c1/offerings")
        client.customers().createToken("c1", 120)
        expect("POST", "merchant/customers/c1/token", mapOf("expires_in" to 120))
        client.customers().createToken("c1")
        expect("POST", "merchant/customers/c1/token")
    }

    @Test
    fun entitlements() {
        responseBody = """{"success":true,"data":[{"id":"e1"}]}"""
        assertEquals(1, client.entitlements().list().size)
        expect("GET", "merchant/entitlements")
        client.entitlements().create(mapOf("identifier" to "pro"))
        expect("POST", "merchant/entitlements", mapOf("identifier" to "pro"))
        client.entitlements().get("e1")
        expect("GET", "merchant/entitlements/e1")
        client.entitlements().update("e1", "Pro")
        expect("PATCH", "merchant/entitlements/e1", mapOf("display_name" to "Pro"))
        client.entitlements().delete("e1")
        expect("DELETE", "merchant/entitlements/e1")
        client.entitlements().attachProducts("e1", listOf("p1"))
        expect("POST", "merchant/entitlements/e1/attach_products", mapOf("product_ids" to listOf("p1")))
        client.entitlements().detachProducts("e1", listOf("p1"))
        expect("POST", "merchant/entitlements/e1/detach_products", mapOf("product_ids" to listOf("p1")))
    }

    @Test
    fun productsAndOfferings() {
        client.products().list()
        expect("GET", "merchant/products")
        client.products().create(mapOf("name" to "Pass"))
        expect("POST", "merchant/products", mapOf("name" to "Pass"))
        client.products().get("p1")
        expect("GET", "merchant/products/p1")
        client.products().update("p1", mapOf("is_active" to false))
        expect("PATCH", "merchant/products/p1", mapOf("is_active" to false))
        client.offerings().list()
        expect("GET", "merchant/offerings")
        client.offerings().create(mapOf("identifier" to "default"))
        expect("POST", "merchant/offerings", mapOf("identifier" to "default"))
        client.offerings().get("o1")
        expect("GET", "merchant/offerings/o1")
        client.offerings().update("o1", mapOf("is_current" to true))
        expect("PATCH", "merchant/offerings/o1", mapOf("is_current" to true))
        client.offerings().delete("o1")
        expect("DELETE", "merchant/offerings/o1")
    }

    @Test
    fun subscriptionRenewalPrompts() {
        client.subscriptions().listRenewalPrompts("s1", 2, 50)
        expect("GET", "merchant/subscriptions/renewal_prompts?subscription_id=s1&page=2&page_size=50")
        client.subscriptions().listRenewalPrompts()
        expect("GET", "merchant/subscriptions/renewal_prompts")
        client.subscriptions().sendRenewalPrompt("s1")
        expect("POST", "merchant/subscriptions/s1/renewal_prompt")
    }

    @Test
    fun metricsExperimentsAndIntegrations() {
        client.metrics().get("2026-01-01", "2026-01-31")
        expect("GET", "merchant/metrics?from=2026-01-01&to=2026-01-31")
        client.metrics().get()
        expect("GET", "merchant/metrics")
        client.experiments().list()
        expect("GET", "merchant/experiments")
        client.experiments().create(mapOf("name" to "A/B"))
        expect("POST", "merchant/experiments", mapOf("name" to "A/B"))
        client.experiments().get("x1")
        expect("GET", "merchant/experiments/x1")
        client.experiments().stop("x1")
        expect("POST", "merchant/experiments/x1/stop")
        client.integrations().list()
        expect("GET", "merchant/integrations")
        client.integrations().put("slack", mapOf("enabled" to true))
        expect("PUT", "merchant/integrations/slack", mapOf("enabled" to true))
        client.integrations().test("slack")
        expect("POST", "merchant/integrations/slack/test")
        client.integrations().delete("slack")
        expect("DELETE", "merchant/integrations/slack")
    }

    @Test
    fun initiateRequestCarriesProductFields() {
        val map = InitiatePaymentRequestDto(10.0, "KES", "mpesa", "R1", "https://x.test", productId = "p1", externalCustomerId = "c1", presentedOfferingId = "default").toMap()
        assertEquals("p1", map["productId"])
        assertEquals("c1", map["externalCustomerId"])
        assertEquals("default", map["presentedOfferingId"])
        assertFalse(InitiatePaymentRequestDto(10.0, "KES", "mpesa", "R1", "https://x.test").toMap().containsKey("productId"))
    }

    @Test
    fun parsesCustomerEntitlementAndSubscriptionEvents() {
        val transferred = WebhookEvent(
            mapOf("id" to "1", "type" to "customer.transferred", "source" to "customer", "data" to mapOf("transferred_from" to listOf("a"), "transferred_to" to listOf("b"))),
        )
        assertTrue(transferred.isCustomerEvent)
        assertEquals(listOf("b"), transferred.customerData?.transferredTo)

        val purchase = WebhookEvent(
            mapOf("id" to "2", "type" to "purchase.completed", "source" to "payment", "data" to mapOf("product_id" to "p1", "entitlement_ids" to listOf("pro"), "amount" to 5)),
        )
        assertTrue(purchase.isEntitlementEvent)
        assertEquals("p1", purchase.entitlementData?.productId)
        assertNull(purchase.toTransaction())

        val granted = WebhookEvent(mapOf("id" to "3", "type" to "entitlement.granted", "source" to "entitlement", "data" to mapOf("id" to "g1")))
        assertEquals("g1", granted.entitlementData?.id)

        val issue = WebhookEvent(
            mapOf(
                "id" to "4",
                "type" to "subscription.billing_issue",
                "source" to "subscription",
                "data" to mapOf("subscription_id" to "s1", "product_id" to "plan", "entitlement_ids" to listOf("pro"), "external_customer_id" to "ext1", "grace_period_expiration_at" to "2026-02-01T00:00:00Z"),
            ),
        )
        assertEquals("plan", issue.subscriptionData?.productId)
        assertEquals("ext1", issue.subscriptionData?.externalCustomerId)
        assertEquals("2026-02-01T00:00:00Z", issue.subscriptionData?.gracePeriodExpirationAt)
    }
}
