package com.cresoty.catpossignpad.data.mapper

import com.cresoty.catpossignpad.domain.model.command.PaymentDetailCommand
import com.cresoty.catpossignpad.domain.model.command.UpsertCustomerPointCommand
import com.cresoty.catpossignpad.remote.model.request.PaymentDetailRequest
import com.cresoty.catpossignpad.remote.model.request.UpsertCustomerPointRequest

fun UpsertCustomerPointCommand.toRequest(): UpsertCustomerPointRequest = when (this) {
    is UpsertCustomerPointCommand.BySleSeq -> UpsertCustomerPointRequest(
        taxNo = taxNo,
        computerName = computerName,
        posVersion = posVersion,
        customerPhone = customerPhone,
        transactionDate = transactionDate,
        sleSeq = sleSeq
    )

    is UpsertCustomerPointCommand.BySinglePayment -> UpsertCustomerPointRequest(
        taxNo = taxNo,
        computerName = computerName,
        posVersion = posVersion,
        customerPhone = customerPhone,
        transactionDate = transactionDate,
        transactionGubn = transactionGubn,
        transactionTime = transactionTime,
        transactionAmount = transactionAmount,
        approvalNumber = approvalNumber
    )

    is UpsertCustomerPointCommand.ByMultiplePayment -> UpsertCustomerPointRequest(
        taxNo = taxNo,
        computerName = computerName,
        posVersion = posVersion,
        customerPhone = customerPhone,
        transactionDate = transactionDate,
        transactionAmount = transactionAmount,
        payments = payments.map { it.toRequest() }
    )
}

fun PaymentDetailCommand.toRequest(): PaymentDetailRequest =
    PaymentDetailRequest(
        transactionGubn = transactionGubn,
        transactionDate = transactionDate,
        transactionTime = transactionTime,
        approvalNumber = approvalNumber,
        transactionAmount = transactionAmount
    )