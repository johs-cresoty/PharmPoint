package com.cresoty.catpospoint.data.mapper

import com.cresoty.catpospoint.domain.model.command.EstimatePointCommand
import com.cresoty.catpospoint.domain.model.command.PaymentDetailCommand
import com.cresoty.catpospoint.domain.model.command.UpsertCustomerPointCommand
import com.cresoty.catpospoint.remote.model.request.EstimatePointRequest
import com.cresoty.catpospoint.remote.model.request.PaymentDetailRequest
import com.cresoty.catpospoint.remote.model.request.UpsertCustomerPointRequest

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

fun EstimatePointCommand.toRequest(): EstimatePointRequest = when (this) {
    is EstimatePointCommand.Single -> EstimatePointRequest(
        taxNo = taxNo,
        computerName = computerName,
        posVersion = posVersion,
        trnDate = trnDate,
        trnGubn = trnGubn,
        trnAmt = trnAmt,
        appNum = appNum
    )
    is EstimatePointCommand.Complex -> EstimatePointRequest(
        taxNo = taxNo,
        computerName = computerName,
        posVersion = posVersion,
        payments = listOf(payments.first.toRequest(), payments.second.toRequest())
    )
}