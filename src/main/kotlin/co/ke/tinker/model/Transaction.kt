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
            data.containsKey("payment_reference") && !data.containsKey("id") -> {
                this.initiationData = InitiationDataDto.fromMap(data)
                this.queryData = null
                this.callbackData = null
                this.status = this.initiationData.status
            }
            data.containsKey("id") && data.containsKey("reference") -> {
                this.initiationData = null
                this.queryData = QueryDataDto.fromMap(data)
                this.callbackData = CallbackDataDto.fromMap(data)
                this.status = this.queryData.status
            }
            else -> {
                this.initiationData = null
                this.queryData = null
                this.callbackData = null
                val statusValue = data["status"] as? String ?: "pending"
                this.status = PaymentStatus.fromString(statusValue)
            }
        }
    }
    
    val isSuccessful: Boolean
        get() = status == PaymentStatus.SUCCESS
    
    val isPending: Boolean
        get() = status == PaymentStatus.PENDING
    
    val isCancelled: Boolean
        get() = status == PaymentStatus.CANCELLED
    
    val isFailed: Boolean
        get() = status == PaymentStatus.FAILED
}

