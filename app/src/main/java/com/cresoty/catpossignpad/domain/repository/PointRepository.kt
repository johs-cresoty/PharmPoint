package com.cresoty.catpossignpad.domain.repository

import com.cresoty.catpossignpad.dataresource.DataResource
import com.cresoty.catpossignpad.domain.model.Customers
import kotlinx.coroutines.flow.Flow

interface PointRepository {
    fun getCustomers(
        computerName: String,
        posVersion: String,
        taxNo: String,
        customerHp: String
    ): Flow<DataResource<Customers>>
}