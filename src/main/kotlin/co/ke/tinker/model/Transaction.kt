package co.ke.tinker.model

import co.ke.tinker.enums.PaymentStatus
import co.ke.tinker.model.dto.CallbackDataDto
import co.ke.tinker.model.dto.InitiationDataDto
import co.ke.tinker.model.dto.QueryDataDto

class Transaction(data: Map<String, Any?>) {
    val status: PaymentStatus
    val initiationData: InitiationDataDto?
    val queryData: QueryDataDto?
    val callbackData: CallbackDataDto?

    init {
        when {
            (data.containsKey("paymentReference") || data.containsKey("payment_reference")) && !data.containsKey("id") -> {
                initiationData = InitiationDataDto.fromMap(data)
                queryData = null
                callbackData = null
                status = initiationData.status
            }
            data.containsKey("id") && data.containsKey("reference") -> {
                initiationData = null
                queryData = QueryDataDto.fromMap(data)
                callbackData = CallbackDataDto.fromMap(data)
                status = queryData.status
            }
            else -> {
                initiationData = null
                queryData = null
                callbackData = null
                val statusValue = data["status"] as? String ?: "pending"
                status = PaymentStatus.fromString(statusValue)
            }
        }
    }

    val isSuccessful: Boolean get() = status == PaymentStatus.SUCCESS
    val isPending: Boolean get() = status == PaymentStatus.PENDING
    val isCancelled: Boolean get() = status == PaymentStatus.CANCELLED
    val isFailed: Boolean get() = status == PaymentStatus.FAILED
}
