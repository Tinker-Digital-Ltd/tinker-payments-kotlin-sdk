# Tinker Payments Kotlin SDK

Official Kotlin SDK for [Tinker Payments API](https://payments.tinker.co.ke/docs).

## Installation

### Gradle (Kotlin DSL)

Add this dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("co.ke.tinker:tinker-payments-kotlin-sdk:0.1.0")
}
```

### Gradle (Groovy)

Add this dependency to your `build.gradle`:

```gradle
dependencies {
    implementation 'co.ke.tinker:tinker-payments-kotlin-sdk:0.1.0'
}
```

### Maven

Add this dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>co.ke.tinker</groupId>
    <artifactId>tinker-payments-kotlin-sdk</artifactId>
    <version>0.1.0</version>
</dependency>
```

### GitHub Packages

To install from GitHub Packages, add the repository to your `build.gradle.kts`:

```kotlin
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/tinker/payments-kotlin-sdk")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("co.ke.tinker:tinker-payments-kotlin-sdk:0.1.0")
}
```

**Note:** You'll need to authenticate with GitHub Packages. Add your GitHub token to your environment variables or Gradle properties.

For Maven, add to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/tinker/payments-kotlin-sdk</url>
    </repository>
</repositories>
```

And add your GitHub token to `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

## Requirements

- Kotlin 1.9+
- Java 11 or higher
- Gradle or Maven

## Quick Start

```kotlin
import co.ke.tinker.Payments

val tinker = Payments(
    apiPublicKey = "your-public-key",
    apiSecretKey = "your-secret-key"
)
```

## Usage

### Initiate a Payment

```kotlin
import co.ke.tinker.Payments
import co.ke.tinker.enums.Gateway
import co.ke.tinker.model.dto.InitiatePaymentRequestDto
import co.ke.tinker.exception.ApiException
import co.ke.tinker.exception.NetworkException

try {
    val request = InitiatePaymentRequestDto(
        amount = 100.00,
        currency = "KES",
        gateway = Gateway.MPESA,
        merchantReference = "ORDER-12345",
        returnUrl = "https://your-app.com/payment/return",
        customerPhone = "+254712345678",
        customerEmail = "customer@example.com",
        transactionDesc = "Payment for order #12345",
        metadata = mapOf("order_id" to "12345")
    )

    val transaction = tinker.transactions().initiate(request)
    val initiationData = transaction.initiationData

    initiationData?.authorizationUrl?.let { url ->
        // Redirect user to authorization URL (Paystack, Stripe, etc.)
        // redirectTo(url)
    }
} catch (e: ApiException) {
    println("API Error: ${e.message}")
} catch (e: NetworkException) {
    println("Network Error: ${e.message}")
}
```

**Note:** The `returnUrl` is where users are redirected after payment completion. Webhooks are configured separately in your dashboard.

### Query a Transaction

```kotlin
import co.ke.tinker.model.dto.QueryPaymentRequestDto

val queryRequest = QueryPaymentRequestDto(
    paymentReference = "TXN-abc123xyz",
    gateway = Gateway.MPESA
)

val transaction = tinker.transactions().query(queryRequest)

if (transaction.isSuccessful) {
    val queryData = transaction.queryData
    println("Amount: ${queryData?.amount} ${queryData?.currency}")
}
```

### Handle Webhooks

Webhooks support multiple event types: payment, subscription, invoice, and settlement. Check the event type and handle accordingly:

```kotlin
import co.ke.tinker.webhook.WebhookEvent
import co.ke.tinker.webhook.dto.PaymentEventDataDto
import co.ke.tinker.webhook.dto.SubscriptionEventDataDto
import co.ke.tinker.webhook.dto.InvoiceEventDataDto
import co.ke.tinker.webhook.dto.SettlementEventDataDto

val event = tinker.webhooks().handleFromRequest(requestBody)

// Check event type
when {
    event.isPaymentEvent -> {
        val paymentData = event.paymentData
        // Handle payment.completed, payment.failed, etc.
    }
    event.isSubscriptionEvent -> {
        val subscriptionData = event.subscriptionData
        // Handle subscription.created, subscription.cancelled, etc.
    }
    event.isInvoiceEvent -> {
        val invoiceData = event.invoiceData
        // Handle invoice.paid, invoice.failed
    }
    event.isSettlementEvent -> {
        val settlementData = event.settlementData
        // Handle settlement.processed
    }
}

// Access event details
println("Event type: ${event.type}")        // e.g., "payment.completed"
println("Event source: ${event.source}")    // e.g., "payment"
println("App ID: ${event.meta.appId}")
println("Signature: ${event.security.signature}")
```

For payment events only, you can convert to a `Transaction` object:

```kotlin
import co.ke.tinker.model.dto.CallbackDataDto

val transaction = tinker.webhooks().handleAsTransaction(requestBody)
transaction?.let {
    if (it.isSuccessful) {
        val callbackData = it.callbackData
        println("Payment successful: ${callbackData?.reference}")
    }
}
```

## Custom HTTP Client

You can use your own HTTP client by passing it to the constructor. However, the current implementation uses OkHttp. To use a custom client, you would need to implement a compatible HTTP client interface or modify the `HttpClient` class.

```kotlin
import co.ke.tinker.http.HttpClient

// Create a custom HTTP client that implements the same interface
val customClient = MyCustomHttpClient()

val tinker = Payments(
    apiPublicKey = "your-public-key",
    apiSecretKey = "your-secret-key",
    httpClient = customClient
)
```

## Kotlin Features

This SDK leverages Kotlin's language features for a better developer experience:

- **Data Classes**: DTOs are implemented as data classes for clean, concise code
- **Nullable Types**: Proper null safety throughout the SDK
- **Extension Properties**: Convenient properties like `isSuccessful`, `isPending`, etc. on `Transaction`
- **Default Parameters**: Cleaner API with default parameters instead of multiple constructors
- **Named Parameters**: Improved readability with named parameters
- **Smart Casts**: Type-safe event data access in webhooks

## Documentation

For detailed API documentation, visit [Tinker Payments API Documentation](https://payments.tinker.co.ke/docs).

## Development

After checking out the repo, run `./gradlew build` to build the project. Then, run `./gradlew test` to run the tests.

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/tinker/payments-kotlin-sdk.

## License

MIT License

