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
- Java 17 or higher
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


## API errors

API exceptions preserve `httpStatus`, `errorCode`, `providerCode`, `requestId`, `retryAfter`, and `outcome`.
Request IDs prefer `error.request_id` over the `X-Request-ID` header. `Retry-After`
is retained as its original header string. Non-JSON HTTP failures retain status and
headers as API errors with an unknown outcome. Existing exception constructors and
legacy code/message access remain compatible.

Only an explicit `not_applied` outcome confirms no mutation. A missing outcome
remains uncertain.

## Managed recurring billing

Existing plan-based subscription methods remain available.

Checkout uses exact integer minor-unit prices (for example, `15000` means EUR 150.00),
an explicit recurring interval (`month` or `year`), and a quantity. The SDK does not
convert prices or apply automatic discounts. Managed responses are unwrapped API
data objects/maps; payment history includes `items`, `snapshot`, `has_more` and
`next_cursor`.

| Method | Purpose |
|---|---|
| `checkout` | Create an exact-price recurring checkout |
| `billingResource` | Read a managed billing resource |
| `changeQuantity` | Change the current quantity |
| `scheduleQuantity` | Schedule a quantity change |
| `clearScheduledQuantity` | Remove a scheduled quantity change |
| `setCancelAtRenewal` | Set cancellation at renewal; pass false to clear it |
| `billingOperation` | Recover the durable result by idempotency key |
| `paymentMethodPortal` | Create a short-lived hosted payment-method link |
| `billingPaymentHistory` | Read one page of captured payments and adjustments |

```kotlin
import co.ke.tinker.model.dto.ManagedCheckout
import co.ke.tinker.exception.ApiException

val billing = payments.subscriptions()
billing.checkout(ManagedCheckout(
    externalCustomerId = "workspace-123", email = "buyer@example.com", name = "Buyer",
    currency = "EUR", unitAmount = 15000L, quantity = 3, interval = "year",
    returnUrl = "https://example.com/billing", idempotencyKey = "checkout-123"
))
billing.changeQuantity("resource-id", 4, "quantity-123")
billing.setCancelAtRenewal("resource-id", false, "resume-123")
billing.paymentMethodPortal("resource-id", "https://example.com/billing", "portal-123")
val page = billing.billingPaymentHistory("resource-id")
// After an uncertain command response, recover using the SAME key:
val operation = billing.billingOperation("quantity-123")
// Catch ApiException and inspect error.httpStatus, error.outcome, etc.
```

`ManagedCheckout` uses `Long` for minor-unit amounts and `Int` for quantities.
Cursor and snapshot default to an empty string and zero for the initial page.

Use a stable, unique idempotency key for each mutation, and reuse it only with the
same payload. After a timeout, retrieve the durable result with the operation lookup
method. Missing or pending results do not prove a charge failed. Do not retry with a
new key to work around an uncertain result. Only an explicit `outcome == "not_applied"`
confirms no mutation; missing outcome remains uncertain. No automatic charge retry is
performed by these methods.

The payment-method portal updates payment details only. It cannot change quantity or
price, and paying an overdue invoice is a separate action through its hosted invoice
URL. Reuse the operation key after an uncertain response; use a new key to deliberately
open a fresh session after expiry. The server checks app ownership and requires a
managed subscription with a dedicated customer.

For payment history, start with an empty cursor and zero snapshot, then retain the
returned snapshot and pass `next_cursor` until `has_more` is false. Amounts are integer
minor units and timestamps are UTC RFC3339. The history includes captured cash payments,
tax-exclusive eligible amounts, successful refunds, and dispute principal withdrawals
and reinstatements (`reverses_id` links a reinstatement to its withdrawal).

Quarantine items with `reconciliation_reason`; commit a scan only after every page has
been verified. The snapshot is an event cutoff, not an immutable provider snapshot.
Rescan from the first page regularly and apply facts idempotently by payment/adjustment
ID. Zero-cash invoices are omitted from items while still advancing pagination. Currency
conversion and customer credits require explicit reconciliation; processing and dispute
fees are excluded from eligible principal. The SDK returns these facts without applying
credits or reconciling them automatically.

### Setup webhook acknowledgement

The dashboard sends a signed `setup.webhook_test` event with `source: "onboarding"`, `data.message`, and setup metadata. Parse it with the webhook handler, verify its signature using the application webhook secret, then acknowledge it with HTTP 200, 202, or 204. Setup events have onboarding data and do not convert to payment transactions. Event classification alone does not authenticate a request. Invalid signatures must be rejected; no payment lookup or fulfillment is needed for a valid setup test.

The signature uses HMAC-SHA256 over the JSON envelope excluding `security`, preserving envelope field order and Go JSON map ordering and escaping.

## Refunds, withdrawals and checkout cancellation

Use `transactions().refund/refunds/getRefund`, `transactions().cancelCheckout/cancellations/getCancellation` and `payouts().methods/quote/requestPayout/list/get` for merchant lifecycle operations. Every creation requires a stable caller-provided idempotency key. SDK payouts withdraw your merchant balance to your own verified payout account and require the owner's explicit app withdrawal grant. Review/processing/unknown statuses do not confirm money movement or provider cancellation.

See the [typed workflow examples and error/retry guidance](docs/merchant-lifecycle.md) and [unreleased lifecycle notes](docs/merchant-lifecycle-release-notes.md). Signed cancellation updates expose typed cancellation data and never convert into payment transactions.
