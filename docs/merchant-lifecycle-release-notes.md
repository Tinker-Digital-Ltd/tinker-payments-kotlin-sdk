# Merchant lifecycle changes — unreleased

- Add typed refund creation/history/get, merchant withdrawal destination/quote/request/list/get, and unpaid checkout cancellation/history/get methods.
- Require caller-provided stable mutation idempotency keys and preserve structured API errors, request IDs and unknown/review states.
- Reuse the existing HTTP/auth transport and keep payment initiation/query/subscription APIs compatible.
- Decode signed `refund.updated.v1`, `withdrawal.updated.v1` and `checkout.cancellation.updated.v1` data separately from payment transactions; preserve raw fields for signature verification.
- Include shared HTTP contract and signed-event fixtures. No package is published by this change.

Release only after the corresponding backend routes/migrations and recovery checks are deployed and the actual sandbox runtime passes the same scenarios. SDK withdrawals remain disabled until the account owner grants the app permission. Manual transfer processing remains an admin responsibility.
