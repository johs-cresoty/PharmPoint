package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.model.command.UpsertCustomerPointCommand
import com.cresoty.catpospoint.domain.repository.PointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class UpsertCustomerPointUseCase @Inject constructor(
    private val repository: PointRepository
) {
    operator fun invoke(
        command: UpsertCustomerPointCommand
    ): Flow<DataResource<String>> {
        return repository.upsertCustomerPoint(command).map { resource ->
            when (resource) {
                is DataResource.Success -> {
                    val balance = resource.data
                        ?.data
                        ?.info
                        ?.firstOrNull()
                        ?.pointBalance
                        ?: "0"
                    DataResource.Success(balance)
                }

                is DataResource.Error -> DataResource.Error(resource.throwable)
                is DataResource.Loading -> DataResource.Loading
            }
        }
    }
}