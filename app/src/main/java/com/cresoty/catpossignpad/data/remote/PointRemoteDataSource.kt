package com.cresoty.catpossignpad.data.remote

import com.cresoty.catpossignpad.data.model.CustomerPointDeltaResponseEntity
import com.cresoty.catpossignpad.data.model.CustomersEntity
import com.cresoty.catpossignpad.data.model.EstimatePointEntity
import com.cresoty.catpossignpad.remote.model.request.EstimatePointRequest
import com.cresoty.catpossignpad.remote.model.request.UpsertCustomerPointRequest
import kotlinx.coroutines.flow.Flow

interface PointRemoteDataSource {
    fun getCustomer(
        computerName: String,
        posVersion: String,
        taxNo: String,
        customerHp: String
    ): Flow<CustomersEntity>

    fun upsertCustomerPoint(
        request: UpsertCustomerPointRequest
    ): Flow<CustomerPointDeltaResponseEntity>

    fun estimatePoint(request: EstimatePointRequest): Flow<EstimatePointEntity>
}