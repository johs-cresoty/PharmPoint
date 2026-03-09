package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.repository.PointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IsCustomersUseCase @Inject constructor(
    private val repository: PointRepository
) {
    operator fun invoke(
        computerName: String,
        posVersion: String,
        taxNo: String,
        customerHp: String
    ): Flow<DataResource<Boolean>> {
        return repository.getCustomers(
            computerName = computerName,
            posVersion = posVersion,
            taxNo = taxNo,
            customerHp = customerHp
        ).map { resource ->
            when (resource) {
                is DataResource.Success -> {
                    val isNotEmpty = resource.data.list.isNotEmpty()
                    DataResource.Success(isNotEmpty)
                }
                is DataResource.Error -> {
                    DataResource.Error(resource.throwable)
                }
                is DataResource.Loading -> {
                    DataResource.Loading
                }
            }
        }
    }

}