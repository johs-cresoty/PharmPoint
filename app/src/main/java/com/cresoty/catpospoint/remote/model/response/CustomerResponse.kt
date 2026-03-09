package com.cresoty.catpospoint.remote.model.response

import com.cresoty.catpospoint.data.model.CustomersEntity
import com.cresoty.catpospoint.data.model.CustomerEntity
import com.cresoty.catpospoint.data.model.CustomerResponseEntity
import com.google.gson.annotations.SerializedName

data class CustomerResponse(
    @SerializedName("MSG")
    val message: String,

    @SerializedName("CODE")
    val code: String,

    @SerializedName("DATA")
    val data: CustomerData?,

    @SerializedName("DTL")
    val detail: String
)

data class CustomerData(
    @SerializedName("LIST")
    val list: List<CustomerDto>
)

data class CustomerDto(
    @SerializedName("CST_CODE")
    val customerCode: String,

    @SerializedName("CST_HP")
    val customerHp: String,

    @SerializedName("CST_NAME")
    val customerName: String,

    @SerializedName("CST_GNDR")
    val gender: String,

    @SerializedName("CST_BRTH")
    val birth: String,

    @SerializedName("PNT_AMT")
    val pointAmount: String
)

fun CustomerResponse.toData(): CustomerResponseEntity =
    CustomerResponseEntity(
        message = message,
        code = code,
        data = data?.toData(),
        detail = detail
    )

fun CustomerData.toData(): CustomersEntity =
    CustomersEntity(
        list = list.map { it.toData() }
    )

fun CustomerDto.toData(): CustomerEntity =
    CustomerEntity(
        customerCode = customerCode,
        customerHp = customerHp,
        customerName = customerName,
        gender = gender,
        birth = birth,
        pointAmount = pointAmount
    )