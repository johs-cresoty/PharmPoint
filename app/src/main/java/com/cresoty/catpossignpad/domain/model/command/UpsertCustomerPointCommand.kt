package com.cresoty.catpossignpad.domain.model.command


sealed class UpsertCustomerPointCommand {
    abstract val taxNo: String
    abstract val computerName: String
    abstract val posVersion: String
    abstract val customerPhone: String
    abstract val transactionDate: String

    data class BySleSeq(
        override val taxNo: String,
        override val computerName: String,
        override val posVersion: String,
        override val customerPhone: String,
        override val transactionDate: String,
        val sleSeq: String
    ) : UpsertCustomerPointCommand()

    data class BySinglePayment(
        override val taxNo: String,
        override val computerName: String,
        override val posVersion: String,
        override val customerPhone: String,
        override val transactionDate: String,
        val transactionTime: String,
        val transactionGubn: String,
        val transactionAmount: String,
        val approvalNumber: String
    ) : UpsertCustomerPointCommand()

    data class ByMultiplePayment(
        override val taxNo: String,
        override val computerName: String,
        override val posVersion: String,
        override val customerPhone: String,
        override val transactionDate: String,
        val transactionAmount: String,
        val payments: List<PaymentDetailCommand>
    ) : UpsertCustomerPointCommand()
}

data class PaymentDetailCommand(
    val transactionGubn: String,
    val transactionDate: String,
    val transactionTime: String,
    val approvalNumber: String,
    val transactionAmount: String
)