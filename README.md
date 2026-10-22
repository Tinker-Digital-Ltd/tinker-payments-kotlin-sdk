# Tinker Payments Kotlin SDK

Kotlin 1.9+ / Java 17+ SDK with automatic authentication, payments, subscriptions and signed webhooks. Keep secret keys on the server.

## Install and configure

Build from source into your local Maven repository:

```sh
git clone git@github.com:Tinker-Digital-Ltd/tinker-payments-kotlin-sdk.git tinker-payments-kotlin-sdk
cd tinker-payments-kotlin-sdk
gradle publishToMavenLocal
```

Then add to your application's `build.gradle.kts`:

```kotlin
repositories { mavenLocal(); mavenCentral() }
dependencies {
    implementation("co.ke.tinker:tinker-payments-kotlin-sdk:0.1.0")
}
```

Test key prefixes select `https://sandbox-api.tinkerpayments.com/v1/`; other keys select `https://api.tinkerpayments.com/v1/`. Override with `Payments(pk, sk, baseUrl = "https://custom-host/v1")`. A custom host gets `/v1` appended when absent.

## Payments

The examples share the same `client`; place executable statements inside an application method/function.

```kotlin
import co.ke.tinker.Payments
import co.ke.tinker.enums.Gateway
import co.ke.tinker.model.dto.InitiatePaymentRequestDto
import co.ke.tinker.model.dto.QueryPaymentRequestDto

val client = Payments("pk_test_xxx", "sk_test_xxx")
val payment = client.transactions().initiate(InitiatePaymentRequestDto(
    amount = 100.0, currency = "KES", gateway = Gateway.MPESA,
    merchantReference = "ORDER-12345",
    returnUrl = "https://merchant.example/payment/return",
    customerPhone = "+254712345678",
))
val result = client.transactions().query(QueryPaymentRequestDto(
    paymentReference = "TXN-abc123xyz", gateway = Gateway.MPESA,
))
```

Payments use currency units. Mpesa requires `customerPhone`; `returnUrl` is the browser destination, while webhooks are configured separately in the dashboard. Hosted providers can return an authorization URL through `payment.initiationData?.authorizationUrl`; check for absent initiation data before redirecting.

Plan subscriptions use `subscriptions().createPlan`, `listPlans`, `create`, `list` and `cancel`, with DTOs in `co.ke.tinker.model.dto`.

## Managed recurring billing

Checkout uses exact integer minor-unit prices (`15000` = EUR 150.00), a `month` or `year` interval, and a quantity. The SDK does not convert prices or apply discounts. Responses are unwrapped data maps/objects.

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

val billing = client.subscriptions()
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
```

`ManagedCheckout` uses `Long` for minor-unit amounts and `Int` for quantities.
Cursor and snapshot default to an empty string and zero for the initial page.

### Trials with a required payment method

Set `trialDays` (1 to 30) to collect a card at checkout and charge first when the trial ends. The SDK sends `trial_days` only when it is above 0, so checkouts without a trial keep working against servers released before trial support. A gateway without trial support returns HTTP 422 `TRIAL_UNSUPPORTED` with `outcome: not_applied`. Limiting a customer to one trial is the app's responsibility.

```kotlin
billing.checkout(ManagedCheckout(
    externalCustomerId = "workspace-123", email = "buyer@example.com", name = "Buyer",
    currency = "EUR", unitAmount = 15000L, quantity = 3, interval = "year",
    returnUrl = "https://example.com/billing", idempotencyKey = "checkout-124",
    trialDays = 14
))
```

Checkout and billing resource responses include a provider-neutral `managed_state` map: `status` (`pending`, `trialing`, `active`, `past_due`, `paused` or `canceled`), `trial_ends_at`, `current_period_end` and `cancel_at_period_end`. Read it instead of raw provider fields; grant access while `status` is `trialing` or `active` and `current_period_end` is in the future. Managed subscription webhooks carry `billing_status` and `trial_ends_at`, and `subscription.trial_will_end` arrives up to three days before the trial ends. The trial's €0 invoice sends `invoice.paid` with amount 0 and is not a payment.

Use a stable key per mutation and reuse it with the same payload after a lost response. Recover through the operation lookup; missing or pending results remain uncertain. Only an explicit `outcome` of `not_applied` confirms no mutation.

The payment-method portal changes payment details only; quantity, price and overdue invoice payment are separate actions. It requires an app-owned managed subscription with a dedicated customer. Reuse the operation key after a lost response and use a new key for a deliberately new session after expiry.

For payment history, keep the returned `snapshot` across `next_cursor` pages until `has_more` is false. Amounts are integer minor units and timestamps are UTC RFC3339. History covers captured cash, tax-exclusive eligible amounts, refunds, and dispute principal withdrawals/reinstatements (`reverses_id` identifies the reversed withdrawal).

Quarantine items with `reconciliation_reason` and verify every page before committing a scan. The snapshot is an event cutoff, not an immutable provider snapshot: rescan from the first page to discover later adjustments, then apply facts idempotently by payment/adjustment ID. Zero-cash invoices advance pagination without an item. Processing/dispute fees are excluded from eligible principal; currency conversion and customer credits need explicit reconciliation.

## Refunds, withdrawals and checkout cancellation

See the [typed workflow guide](docs/merchant-lifecycle.md) for refunds, payout quotes/requests and cancellation capability/history. A payout withdraws your merchant wallet balance to your own verified account and requires the owner's explicit app withdrawal permission. Each creation needs a persisted idempotency key; review, processing and unknown states are not completed money movement or provider cancellation.

## Webhooks

```kotlin
val event = client.webhooks().handle(requestBody)
check(client.webhooks().verifySignature(event, webhookSecret)) { "Invalid webhook signature" }
val transaction = event.toTransaction() // null for setup/lifecycle events
```

Verify the signature before consuming fields or converting the event to a transaction; reuse the parsed event rather than reading the request body twice. Deduplicate by event ID. The SDK verifies HMAC-SHA256 over the envelope excluding `security`, using the server's field order, map ordering and escaping.

After verification, acknowledge `setup.webhook_test` (`source: onboarding`) with HTTP 200, 202 or 204; it needs no payment lookup or fulfillment. The signed lifecycle events `refund.updated.v1`, `withdrawal.updated.v1` and `checkout.cancellation.updated.v1` carry `data.resource` with `source: payment`. Their typed resources never convert into payment transactions. Poll request IDs if delivery is delayed.

## Errors and response metadata

API errors retain `httpStatus`, `errorCode`, `providerCode`, `requestId`, `retryAfter`, `outcome` and `details`. Request IDs prefer the response body over `X-Request-ID`; `Retry-After` remains its original string. Non-JSON HTTP failures retain status/headers and an uncertain outcome. Only an explicit `not_applied` outcome confirms no mutation. `details` retains the full error envelope. Managers expose `lastMeta`; `client.getLastAuthMeta()` exposes authentication response metadata.

```kotlin
import co.ke.tinker.exception.ApiException

try {
    client.transactions().query(QueryPaymentRequestDto("TXN-abc123xyz", Gateway.MPESA))
} catch (error: ApiException) {
    System.err.println("${error.errorCode}: ${error.message}")
}
```

## Development

`gradle check build` runs tests, ktlint and packaging. Use JDK 17 and Gradle; the repository has no Gradle wrapper. Lifecycle tests compare bundled fixtures to the canonical checkout when present; set `TINKER_LIFECYCLE_FIXTURE` to use another fixture path. Signed webhook tests include tampering checks.

MIT License.
