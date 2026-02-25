package com.cresoty.catpossignpad.data.remote

import com.cresoty.catpossignpad.data.model.CustomersEntity
import kotlinx.coroutines.flow.Flow

interface PointRemoteDataSource {
    fun getCustomer(
        computerName: String,
        posVersion: String,
        taxNo: String,
        customerHp: String
    ): Flow<CustomersEntity>
}