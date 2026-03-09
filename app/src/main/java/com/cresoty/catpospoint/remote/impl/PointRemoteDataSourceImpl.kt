package com.cresoty.catpospoint.remote.impl

import android.os.Build
import com.cresoty.catpospoint.BuildConfig
import com.cresoty.catpospoint.data.model.CustomerPointDeltaResponseEntity
import com.cresoty.catpospoint.data.model.CustomersEntity
import com.cresoty.catpospoint.data.model.EstimatePointEntity
import com.cresoty.catpospoint.data.model.PointAmountSettingEntity
import com.cresoty.catpospoint.data.model.PointBalanceEntity
import com.cresoty.catpospoint.data.model.PointSaveSettingEntity
import com.cresoty.catpospoint.data.remote.PointRemoteDataSource
import com.cresoty.catpospoint.remote.api.CatposCloudApi
import com.cresoty.catpospoint.remote.exception.EstimatePointRetryableException
import com.cresoty.catpospoint.remote.model.request.EstimatePointRequest
import com.cresoty.catpospoint.remote.model.request.UpsertCustomerPointRequest
import com.cresoty.catpospoint.remote.model.response.toData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class PointRemoteDataSourceImpl @Inject constructor(
    private val apiService: CatposCloudApi
) : PointRemoteDataSource {

    private val computerName = "${Build.BRAND}_${Build.MODEL}"
    private val posVersion = BuildConfig.VERSION_NAME
    private val posGubn = "CP"

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

    override fun estimatePoint(request: EstimatePointRequest): Flow<EstimatePointEntity> = flow {
        val response = apiService.estimatePoint(request)
        val code = response.code ?: "-9999"
        when {
            code == "8888" || code == "9303" -> throw EstimatePointRetryableException(code)
            code == "0000"                   -> emit(response.toData())
            else -> throw Exception("estimatePoint failed: code=$code msg=${response.message}")
        }
    }

    override fun getPointSaveSetting(taxNo: String): Flow<PointSaveSettingEntity> = flow {
        val response = apiService.getPointSaveSetting(
            taxNo        = taxNo,
            computerName = computerName,
            posVersion   = posVersion,
            posGubn      = posGubn
        )
        val code = response.code ?: "-9999"
        if (code == "0000") {
            emit(response.toData())
        } else {
            throw Exception("getPointSaveSetting failed: code=$code msg=${response.message}")
        }
    }

    override fun getPointAmountSetting(taxNo: String): Flow<PointAmountSettingEntity> = flow {
        val response = apiService.getPointAmountSetting(
            taxNo        = taxNo,
            computerName = computerName,
            posVersion   = posVersion,
            posGubn      = posGubn
        )
        val code = response.code ?: "-9999"
        if (code == "0000") {
            emit(response.toData())
        } else {
            throw Exception("getPointAmountSetting failed: code=$code msg=${response.message}")
        }
    }

    override fun getPointBalance(taxNo: String, customerPhone: String): Flow<PointBalanceEntity> = flow {
        val response = apiService.getPointBalance(
            taxNo         = taxNo,
            customerPhone = customerPhone,
            computerName  = computerName,
            posVersion    = posVersion,
            posGubn       = posGubn
        )
        val code = response.code ?: "-9999"
        if (code == "0000") {
            emit(response.toData())
        } else {
            throw Exception("getPointBalance failed: code=$code msg=${response.message}")
        }
    }
}