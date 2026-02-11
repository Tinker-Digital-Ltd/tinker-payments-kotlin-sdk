package co.ke.tinker.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConfigurationTest {

    @Test
    fun `initializes with sandbox for test keys`() {
        val config = Configuration("pk_test_123", "sk_test_456")

        assertEquals("pk_test_123", config.apiPublicKey)
        assertEquals("sk_test_456", config.apiSecretKey)
        assertEquals("https://sandbox-api.tinkerpayments.com/v1/", config.baseUrl)
        assertEquals("https://sandbox-api.tinkerpayments.com/v1/auth/token", config.authUrl)
        assertEquals("sk_test_456", config.apiKey)
    }

    @Test
    fun `uses production for live keys`() {
        val config = Configuration("pk_live_123", "sk_live_456")
        assertEquals("https://api.tinkerpayments.com/v1/", config.baseUrl)
    }

    @Test
    fun `supports custom base url`() {
        val config = Configuration("pk_test_123", "sk_test_456", "http://localhost:8080")
        assertEquals("http://localhost:8080/v1/", config.baseUrl)
        assertEquals("http://localhost:8080/v1/auth/token", config.authUrl)
    }
}
