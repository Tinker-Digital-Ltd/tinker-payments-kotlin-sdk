package co.ke.tinker.enums

enum class Gateway(val value: String) {
    MPESA("mpesa"),
    PAYSTACK("paystack"),
    STRIPE("stripe"),
    PESAPAL("pesapal"),
    ;

    override fun toString(): String = value
}
