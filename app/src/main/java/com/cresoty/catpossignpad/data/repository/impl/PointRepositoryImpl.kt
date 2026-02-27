package com.cresoty.catpossignpad.data.repository.impl

import com.cresoty.catpossignpad.data.mapper.toRequest
import com.cresoty.catpossignpad.data.remote.PointRemoteDataSource
import com.cresoty.catpossignpad.dataresource.DataResource
import com.cresoty.catpossignpad.domain.model.CustomerPointDeltaResult
import com.cresoty.catpossignpad.domain.model.Customers
import com.cresoty.catpossignpad.domain.model.command.UpsertCustomerPointCommand
import com.cresoty.catpossignpad.domain.repository.PointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject


internal class PointRepositoryImpl @Inject constructor(
    private val pointRemoteDataSource: PointRemoteDataSource
) : PointRepository {
    override fun getCustomers(
        computerName: String,
        posVersion: String,
        taxNo: String,
        customerHp: String
    ): Flow<DataResource<Customers>> =
        pointRemoteDataSource.getCustomer(
            computerName = computerName,
            posVersion = posVersion,
            taxNo = taxNo,
            customerHp = customerHp
        ).map { entity ->
            DataResource.Success(entity.toDomain()) as DataResource<Customers>
        }
            .onStart { emit(DataResource.Loading) }
            .catch { e ->
                emit(DataResource.Error(e))
            }

    override fun upsertCustomerPoint(
        command: UpsertCustomerPointCommand
    ): Flow<DataResource<CustomerPointDeltaResult>> =
        pointRemoteDataSource.upsertCustomerPoint(command.toRequest())
            .map { entity ->
                DataResource.Success(entity.toDomain()) as DataResource<CustomerPointDeltaResult>
            }
            .onStart { emit(DataResource.Loading) }
            .catch { e -> emit(DataResource.Error(e)) }

}