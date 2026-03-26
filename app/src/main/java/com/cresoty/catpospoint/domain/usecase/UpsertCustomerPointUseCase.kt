package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.device.DeviceInfoProvider
import com.cresoty.catpospoint.domain.model.command.PaymentDetailCommand
import com.cresoty.catpospoint.domain.model.command.UpsertCustomerPointCommand
import com.cresoty.catpospoint.domain.repository.PointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class UpsertCustomerPointUseCase @Inject constructor(
    private val repository: PointRepository,
    private val deviceInfoProvider: DeviceInfoProvider
) {
    operator fun invoke(
        taxNo: String,
        customerPhone: String,
        transactionDate: String,
        transactionUniqueNumber: String,
        transactionMethod: String,
        transactionTime: String,
        transactionAmount: String,
        approvalNumber: String,
        complexTranInfo: Pair<PaymentDetailCommand, PaymentDetailCommand>?
    ): Flow<DataResource<String>> {
        val command = complexTranInfo?.let { pair ->
            if (transactionUniqueNumber.isNotEmpty()) {
                UpsertCustomerPointCommand.BySleSeq(
                    taxNo = taxNo,
                    computerName = deviceInfoProvider.computerName,
                    posVersion = deviceInfoProvider.posVersion,
                    customerPhone = customerPhone,
                    transactionDate = transactionDate,
                    sleSeq = transactionUniqueNumber
                )
            } else {
                UpsertCustomerPointCommand.ByMultiplePayment(
                    taxNo = taxNo,
                    computerName = deviceInfoProvider.computerName,
                    posVersion = deviceInfoProvider.posVersion,
                    customerPhone = customerPhone,
                    transactionDate = transactionDate,
                    transactionAmount = transactionAmount,
                    payments = listOf(pair.first, pair.second)
                )
            }
        } ?: if (transactionUniqueNumber.isNotEmpty()) {
            UpsertCustomerPointCommand.BySleSeq(
                taxNo = taxNo,
                computerName = deviceInfoProvider.computerName,
                posVersion = deviceInfoProvider.posVersion,
                customerPhone = customerPhone,
                transactionDate = transactionDate,
                sleSeq = transactionUniqueNumber
            )
        } else {
            UpsertCustomerPointCommand.BySinglePayment(
                taxNo = taxNo,
                computerName = deviceInfoProvider.computerName,
                posVersion = deviceInfoProvider.posVersion,
                customerPhone = customerPhone,
                transactionDate = transactionDate,
                transactionGubn = transactionMethod,
                transactionTime = transactionTime,
                transactionAmount = transactionAmount,
                approvalNumber = approvalNumber
            )
        }

        return repository.upsertCustomerPoint(command).map { resource ->
            when (resource) {
                is DataResource.Success -> {
                    val balance = resource.data
                        ?.data
                        ?.info
                        ?.firstOrNull()
                        ?.pointBalance
                        ?: "0"
                    DataResource.Success(balance)
                }
                is DataResource.Error -> DataResource.Error(resource.throwable)
                is DataResource.Loading -> DataResource.Loading
            }
        }
    }
}