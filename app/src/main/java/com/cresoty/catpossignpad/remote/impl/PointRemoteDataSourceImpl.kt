package com.cresoty.catpossignpad.remote.impl

import com.cresoty.catpossignpad.data.model.CustomerPointDeltaResponseEntity
import com.cresoty.catpossignpad.data.model.CustomersEntity
import com.cresoty.catpossignpad.data.remote.PointRemoteDataSource
import com.cresoty.catpossignpad.remote.api.CatposCloudApi
import com.cresoty.catpossignpad.remote.model.request.UpsertCustomerPointRequest
import com.cresoty.catpossignpad.remote.model.response.toData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class PointRemoteDataSourceImpl @Inject constructor(
    private val apiService: CatposCloudApi
) : PointRemoteDataSource {
    override fun getCustomer(
        computerName: String,
        posVersion: String,
        taxNo: String,
        customerHp: String
    ): Flow<CustomersEntity> = flow {
        val response = apiService.getCustomer(
            computerName = computerName,
            posVersion = posVersion,
            taxNo = taxNo,
            customerHp = customerHp
        )
        if (response.code == "0000" && response.data != null) {

            val items = response.data.list.map { it.toData() }

            emit(
                CustomersEntity(
                    list = items
                )
            )
        } else {
            throw Exception(response.message ?: "Unknown error")
        }
    }

    override fun upsertCustomerPoint(request: UpsertCustomerPointRequest)
            : Flow<CustomerPointDeltaResponseEntity> = flow {
        val response = apiService.upsertCustomerPoint(request)

        if (response.code == "0000" && response.data != null) {
            emit(response.toData())
        } else {
            throw Exception(response.message ?: "Unknown error")
        }
    }
}