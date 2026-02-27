package com.cresoty.catpossignpad.domain.repository

import com.cresoty.catpossignpad.dataresource.DataResource
import com.cresoty.catpossignpad.domain.model.CustomerPointDeltaResult
import com.cresoty.catpossignpad.domain.model.Customers
import com.cresoty.catpossignpad.domain.model.EstimatePointResult
import com.cresoty.catpossignpad.domain.model.command.EstimatePointCommand
import com.cresoty.catpossignpad.domain.model.command.UpsertCustomerPointCommand
import kotlinx.coroutines.flow.Flow

interface PointRepository {
    fun getCustomers(
        computerName: String,
        posVersion: String,
        taxNo: String,
        customerHp: String
    ): Flow<DataResource<Customers>>


    fun upsertCustomerPoint(
        command: UpsertCustomerPointCommand
    ): Flow<DataResource<CustomerPointDeltaResult>>

    fun estimatePoint(command: EstimatePointCommand): Flow<DataResource<EstimatePointResult?>>

}