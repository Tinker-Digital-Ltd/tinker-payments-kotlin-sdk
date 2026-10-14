package co.ke.tinker.webhook.dto

data class OnboardingEventDataDto(val message: String) {
    companion object {
        fun fromMap(data: Map<String, Any?>): OnboardingEventDataDto =
            OnboardingEventDataDto(data["message"] as? String ?: "")
    }

    fun toMap(): Map<String, Any?> = mapOf("message" to message)
}
