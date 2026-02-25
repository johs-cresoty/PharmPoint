package com.cresoty.catpossignpad.data.model

import com.cresoty.catpossignpad.data.mapper.DataMapper
import com.cresoty.catpossignpad.domain.model.Customer
import com.cresoty.catpossignpad.domain.model.Customers


data class CustomerResponseEntity(
    val message: String,
    val code: String,
    val data: CustomersEntity?,
    val detail: String
)

data class CustomersEntity(
    val list: List<CustomerEntity>
) : DataMapper<Customers> {
    override fun toDomain(): Customers =
        Customers(
            list = list.map { it.toDomain() }
        )

}

data class CustomerEntity(
    val customerCode: String,
    val customerHp: String,
    val customerName: String,
    val gender: String,
    val birth: String,
    val pointAmount: String
) : DataMapper<Customer> {
    override fun toDomain(): Customer =
        Customer(
            customerCode = customerCode,
            customerHp = customerHp,
            customerName = customerName,
            gender = gender,
            birth = birth,
            pointAmount = pointAmount
        )

}