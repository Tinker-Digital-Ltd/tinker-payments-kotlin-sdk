package co.ke.tinker.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ConfigurationTest {
    @Test
    fun testInitialize() {
        val config = Configuration("pk_test_123", "sk_test_456")

        assertEquals("pk_test_123", config.apiPublicKey)
        assertEquals("sk_test_456", config.apiSecretKey)
        assertEquals("${Endpoints.API_BASE_URL}/", config.baseUrl)
    }

    @Test
    fun testGetApiKey() {
        val config = Configuration("pk_test_123", "sk_test_456")

        assertEquals("sk_test_456", config.apiKey)
    }
}

