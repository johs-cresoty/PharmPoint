package com.cresoty.catpospoint.domain.usecase

import com.cresoty.catpospoint.dataresource.DataResource
import com.cresoty.catpospoint.domain.repository.PointRepository
import com.cresoty.catpospoint.domain.repository.SocketResponseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

/**
 * 고객 잔액 조회 후 CAT으로 고객 정보(전화번호 + 고객코드) 응답 전송.
 * CatRequestCustomer(CAT 002) 전문에 대한 응답.
 */
class SendCATCustomerInfoUseCase @Inject constructor(
    private val pointRepository: PointRepository,
    private val socketResponseRepository: SocketResponseRepository,
) {
    operator fun invoke(bizNo: String, phoneNumber: String): Flow<DataResource<Unit>> =
        pointRepository.getPointBalance(taxNo = bizNo, customerPhone = phoneNumber)
            .transform { resource ->
                when (resource) {
                    is DataResource.Success -> {
                        socketResponseRepository.sendCATCustomerInfo(
                            customerPhone = resource.data.customerPhone,
                            customerCode  = resource.data.customerCode,
                        )
                        emit(DataResource.Success(Unit))
                    }
                    is DataResource.Error   -> emit(DataResource.Error(resource.throwable))
                    is DataResource.Loading -> emit(DataResource.Loading)
                }
            }
}
