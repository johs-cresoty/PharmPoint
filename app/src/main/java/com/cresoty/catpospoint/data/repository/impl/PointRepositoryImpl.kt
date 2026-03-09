package com.cresoty.catpospoint.data.repository.impl

import com.cresoty.catpospoint.data.mapper.toRequest
import com.cresoty.catpospoint.data.remote.PointRemoteDataSource
import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.model.CustomerPointDeltaResult
import com.cresoty.catpospoint.domain.model.Customers
import com.cresoty.catpospoint.domain.model.EstimatePointResult
import com.cresoty.catpospoint.domain.model.PointAmountSettingResult
import com.cresoty.catpospoint.domain.model.PointBalanceResult
import com.cresoty.catpospoint.domain.model.PointSaveSettingResult
import com.cresoty.catpospoint.domain.model.command.EstimatePointCommand
import com.cresoty.catpospoint.domain.model.command.UpsertCustomerPointCommand
import com.cresoty.catpospoint.domain.repository.PointRepository
import com.cresoty.catpospoint.remote.exception.EstimatePointRetryableException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
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

    override fun estimatePoint(
        command: EstimatePointCommand
    ): Flow<DataResource<EstimatePointResult?>> =
        pointRemoteDataSource.estimatePoint(command.toRequest())
            .retryWhen { cause, attempt ->
                // attempt: 0-based. 3회 총 시도(초기 1 + retry 2)
                if (cause is EstimatePointRetryableException && attempt < 2L) {
                    delay(if (attempt == 0L) 1_000L else 2_000L)
                    true
                } else false
            }
            .map { entity ->
                DataResource.Success(entity.toDomain()) as DataResource<EstimatePointResult?>
            }
            .onStart { emit(DataResource.Loading) }
            .catch { e ->
                when (e) {
                    // 재시도 소진 → 에러 아님, graceful advance
                    is EstimatePointRetryableException -> emit(DataResource.Success(null))
                    else -> emit(DataResource.Error(e))
                }
            }

    override fun getPointSaveSetting(taxNo: String): Flow<DataResource<PointSaveSettingResult>> =
        pointRemoteDataSource.getPointSaveSetting(taxNo)
            .map { DataResource.Success(it.toDomain()) as DataResource<PointSaveSettingResult> }
            .onStart { emit(DataResource.Loading) }
            .catch { e -> emit(DataResource.Error(e)) }

    override fun getPointAmountSetting(taxNo: String): Flow<DataResource<PointAmountSettingResult>> =
        pointRemoteDataSource.getPointAmountSetting(taxNo)
            .map { DataResource.Success(it.toDomain()) as DataResource<PointAmountSettingResult> }
            .onStart { emit(DataResource.Loading) }
            .catch { e -> emit(DataResource.Error(e)) }

    override fun getPointBalance(taxNo: String, customerPhone: String): Flow<DataResource<PointBalanceResult>> =
        pointRemoteDataSource.getPointBalance(taxNo = taxNo, customerPhone = customerPhone)
            .map { DataResource.Success(it.toDomain()) as DataResource<PointBalanceResult> }
            .onStart { emit(DataResource.Loading) }
            .catch { e -> emit(DataResource.Error(e)) }

}