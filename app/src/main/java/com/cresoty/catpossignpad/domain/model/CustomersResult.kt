package com.cresoty.catpossignpad.domain.model


data class CustomersResult(
    val message: String,
    val code: String,
    val data: Customers?,
    val detail: String
)

data class Customers(
    val list: List<Customer>
)

data class Customer(
    val customerCode: String,
    val customerHp: String,
    val customerName: String,
    val gender: String,
    val birth: String,
    val pointAmount: String
)