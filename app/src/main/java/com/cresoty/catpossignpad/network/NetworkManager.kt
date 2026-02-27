package com.cresoty.catpossignpad.network

import android.os.Build
import android.util.Log
import com.cresoty.catpossignpad.BuildConfig
import com.cresoty.catpossignpad.model.state.ConfigState
import com.cresoty.catpossignpad.remote.api.CatposCloudApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject
import kotlin.concurrent.Volatile

interface NetworkManagerDelegate {
    fun onNetworkError(error: Exception)
}

class NetworkManager @Inject constructor(
    configState: StateFlow<ConfigState>,
    appScope: CoroutineScope
) {
    @Volatile
    private var latestConfig: ConfigState = configState.value

    private val CMPTR_NAME = "${Build.BRAND}_${Build.MODEL}"
    private val POS_VER =
        Class.forName("com.cresoty.catpossignpad.BuildConfig").getField("VERSION_NAME")
            .get(null) as String
    private val RES_CODE_NULL = "-9999"

    private val catposService =
        RetrofitFactory.catposRetrofit(BuildConfig.DEBUG, CMPTR_NAME, POS_VER).create(
            CatposCloudApi::class.java
        )
    private var delegate: NetworkManagerDelegate? = null

    init {
        configState
            .onEach {
                latestConfig = it
            }
            .launchIn(appScope)
    }

    /**
     * 에러 발생시 얼럿 다이얼로그 호출 및 비동기 작동을 위한 함수
     * 필요없다면 제거
     *
     * @param functionName
     * @param isShowAlert
     * @param block
     */
    private suspend fun safeNetworkCall(
        functionName: String,
        isShowAlert: Boolean = true,
        block: suspend () -> Unit
    ) {
        try {
            block()
        } catch (e: Exception) {
            Log.e("SocketDebug", "네트워크 예외 발생 in $functionName", e)
            Timber.e(e, "NetworkManager Error in $functionName")
            if (isShowAlert) delegate?.onNetworkError(e)
        }
    }

    /**
     * 포인트 설정 조회 : 적립 여부
     *
     * @param onDataReceived
     */
    suspend fun requestPointSetting(
        onDataReceived: suspend (String, Boolean) -> Unit
    ) {
        safeNetworkCall("requestCatposSetting") {
            val response = catposService.requestPointSaveSetting(
                taxno = latestConfig.bizNo
            )
            val rescode = response.body()?.CODE ?: RES_CODE_NULL
            val isSave = (response.body()?.DATA?.INFO?.get(0)?.PNT_GUBN ?: "") != "NON"

            onDataReceived(rescode, isSave)
        }
    }

    /**
     * 포인트 설정 조회 : 최소금액
     *
     * @param onDataReceived
     */
    suspend fun requestPointAmountSetting(
        onDataReceived: suspend (String, Int) -> Unit
    ) {
        safeNetworkCall("requestPointAmountSetting") {
            val response = catposService.requestPointAmountSetting(
                taxno = latestConfig.bizNo
            )

            val rescode = response.body()?.CODE ?: RES_CODE_NULL

            val list = response.body()?.DATA?.INFO?.map {
                it.BASE_AMT.toIntOrNull() ?: 0
            } ?: emptyList()
            val min = list.minOrNull() ?: 20000

            onDataReceived(rescode, min)
        }

    }

    /**
     * 포인트 잔액 체크
     *
     * @param cst_hp
     * @param onDataReceived
     */
    suspend fun requestPointBalanceCheck(
        cst_hp: String,
        onDataReceived: (String, String) -> Unit
    ) {
        safeNetworkCall("requestPointBalanceCheck") {
            val response = catposService.requestCheckPointBalance(
                taxno = latestConfig.bizNo,
                cst_hp = cst_hp
            )

            val rescode = response.body()?.CODE ?: RES_CODE_NULL
            val balance = response.body()?.DATA?.INFO?.get(0)?.PNT_BLC ?: "0"

            onDataReceived(rescode, balance)
        }
    }


}