package com.cresoty.catpossignpad.domain.model.command

sealed class EstimatePointCommand {
    abstract val taxNo: String
    abstract val computerName: String
    abstract val posVersion: String

    data class Single(
        override val taxNo: String,
        override val computerName: String,
        override val posVersion: String,
        val trnDate: String,
        val trnGubn: String,
        val trnAmt: String,
        val appNum: String
    ) : EstimatePointCommand()

    data class Complex(
        override val taxNo: String,
        override val computerName: String,
        override val posVersion: String,
        val payments: Pair<PaymentDetailCommand, PaymentDetailCommand>
    ) : EstimatePointCommand()
}
