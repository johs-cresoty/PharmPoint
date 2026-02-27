package com.cresoty.catpossignpad.network

import android.os.Build
import android.util.Log
import com.cresoty.catpossignpad.BuildConfig
import com.cresoty.catpossignpad.model.state.ConfigState
import com.cresoty.catpossignpad.remote.api.CatposCloudApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
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
        } catch (e: RetryableCodeException) {
            // retry 소진 후 onFailed로 이미 처리됨 - 여기서는 무시
            Log.d("SocketDebug", "retry 소진 - code=${e.code} in $functionName")
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

    /**
     * 복합결제 포이트 적립 요청
     *
     * @param trn_date
     * @param trn_time
     * @param trn_amt
     * @param cst_hp
     * @param pair
     * @param onDataReceived
     */
    suspend fun requestPointDeltaComplex(
        trn_date: String,
        trn_time: String,
        trn_amt: String,
        cst_hp: String,
        pair: Pair<HashMap<String, String>, HashMap<String, String>>,
        onDataReceived: (String, String) -> Unit
    ) {
        safeNetworkCall("requestPointDeltaComplex") {
            val request = CatposJSONFactory.makePointDeltaRequestComplex(
                taxno = latestConfig.bizNo,
                cmptr_name = CMPTR_NAME,
                pos_ver = POS_VER,
                trn_date = trn_date,
                trn_time = trn_time,
                trn_amt = trn_amt,
                cst_hp = cst_hp,
                pair = pair,
            )

            val response = catposService.requestPointDeltaComplex(request)

            val rescode = response.body()?.CODE ?: RES_CODE_NULL
            val balance = response.body()?.DATA?.INFO?.get(0)?.PNT_BLC ?: "0"

            onDataReceived(rescode, balance)
        }
    }

    /**
     * 일반결제 포인트 적립 요청
     * SLE_SEQ 받아오는 데 성공했을 시 SLE_SEQ 활용
     * 실패시 승인번호, 일자, 금액, 결제수단 활용
     *
     * @param sle_seq
     * @param trn_date
     * @param cst_hp
     * @param trn_amt
     * @param app_num
     * @param trn_gubn
     * @param onDataReceived
     */
    suspend fun requestPointDelta(
        sle_seq: String,
        trn_date: String,
        cst_hp: String,
        trn_amt: String,
        app_num: String,
        trn_gubn: String,
        onDataReceived: (String, String) -> Unit
    ) {
        safeNetworkCall("requestSavePoint") {
            val request = if (sle_seq.isNotEmpty()) {
                CatposJSONFactory.makePointDeltaRequest(
                    taxno = latestConfig.bizNo,
                    cmptr_name = CMPTR_NAME,
                    pos_ver = POS_VER,
                    sle_seq = sle_seq,
                    trn_date = trn_date,
                    cst_hp = cst_hp
                )
            } else {
                CatposJSONFactory.makePointDeltaRequest(
                    taxno = latestConfig.bizNo,
                    cmptr_name = CMPTR_NAME,
                    pos_ver = POS_VER,
                    app_num = app_num,
                    trn_date = trn_date,
                    trn_amt = trn_amt,
                    trn_gubn = trn_gubn,
                    cst_hp = cst_hp
                )
            }

            val response = catposService.requestPointDelta(request)

            val rescode = response.body()?.CODE ?: RES_CODE_NULL
            val balance = response.body()?.DATA?.INFO?.get(0)?.PNT_BLC ?: "0"
            onDataReceived(rescode, balance)
        }

    }

    /**
     * 복합결제 예상적립금 조회
     *
     * @param pair
     * @param onDataReceived
     */
    suspend fun requestExpectSaveAmountCheckComplex(
        pair: Pair<HashMap<String, String>, HashMap<String, String>>,
        onFailed: ((String) -> Unit)? = null,
        onDataReceived: (String, String, String) -> Unit
    ) {
        safeNetworkCall("requestExpectSaveAmountCheckComplex") {
            retry(
                maxAttempts = 3,
                initialDelayMs = 1_000,
                shouldRetry = { t -> t is RetryableCodeException && t.code in listOf("8888", "9303") }
            ) { remain ->
                val request = CatposJSONFactory.makeExpectSaveAmountRequestComplex(
                    taxno = latestConfig.bizNo,
                    cmptr_name = CMPTR_NAME,
                    pos_ver = POS_VER,
                    pair = pair
                )

                val response = catposService.requestExpectSaveAmountComplex(request)

                val rescode = response.body()?.CODE ?: RES_CODE_NULL

                if (rescode == "8888" || rescode == "9303") {
                    if (remain == 0) onFailed?.invoke(rescode)
                    throw RetryableCodeException(rescode)
                }

                val pnt_amt = response.body()?.DATA?.INFO?.get(0)?.PNT_AMT ?: ""
                val sle_seq = response.body()?.DATA?.INFO?.get(0)?.SLE_SEQ ?: ""

                onDataReceived(rescode, pnt_amt, sle_seq)
            }
        }

    }

    /**
     * 일반결제 예상적립금 조회
     *
     * @param trn_date
     * @param trn_gubn
     * @param trn_amt
     * @param app_num
     * @param onDataReceived
     */
    suspend fun requestExpectSaveAmountCheck(
        trn_date: String,
        trn_gubn: String,
        trn_amt: String,
        app_num: String,
        onFailed: ((String) -> Unit)? = null,
        onDataReceived: (String, String, String) -> Unit
    ) {
        safeNetworkCall("requestSaveAmountCheck") {
            val request = CatposJSONFactory.makeExpectSaveAmountRequest(
                taxno = latestConfig.bizNo,
                cmptr_name = CMPTR_NAME,
                pos_ver = POS_VER,
                trn_date = trn_date,
                trn_gubn = trn_gubn,
                trn_amt = trn_amt,
                app_num = app_num
            )

            ////////////////////////////////////////////////////////////////////
            // TODO : 서버에 판매데이터가 늦게 들어가는 문제로 인해 발생하는 응답코드 "8888" / ApprovalNotExistException 승인 정보가 없습니다.
            //  임시대응... 최대 3회까지 초당 1회 재시도
            // 8888 예기치 않는 오류 발생 시 -> 다음 스텝으로 바로 보내기
            // 9303 판매 승인 정보 없을 때 -> 재시도 2회 -> 다음 스텝
            retry(
                maxAttempts = 3,
                initialDelayMs = 1_000,
                shouldRetry = { t -> t is RetryableCodeException && t.code in listOf("8888", "9303") }
            ) { remain ->
                val response = catposService.requestExpectSaveAmount(request)
                val rescode = response.body()?.CODE ?: RES_CODE_NULL

//                // 테스트용 - 8888 강제 주입 (테스트 후 반드시 제거)
//                val rescode = if (BuildConfig.DEBUG) "8888" else (response.body()?.CODE ?: RES_CODE_NULL)

                if (rescode == "8888" || rescode == "9303") {
                    if (remain == 0) onFailed?.invoke(rescode)
                    throw RetryableCodeException(rescode)
                }

                val pnt_amt = response.body()?.DATA?.INFO?.get(0)?.PNT_AMT ?: ""
                val sle_seq = response.body()?.DATA?.INFO?.get(0)?.SLE_SEQ ?: ""
                onDataReceived(rescode, pnt_amt, sle_seq)
            }
            ////////////////////////////////////////////////////////////////////
        }
    }

    class RetryableCodeException(val code: String) : RuntimeException("retryable code=$code")

    suspend fun <T> retry(
        maxAttempts: Int = 3,
        initialDelayMs: Long = 300,
        backoffFactor: Double = 2.0,
        shouldRetry: (Throwable) -> Boolean = { true },
        block: suspend (Int) -> T
    ): T {
        var delayMs = initialDelayMs
        repeat(maxAttempts) { attempt ->
            val remain = maxAttempts - attempt - 1  // 2, 1, 0
            try {
                return block(remain)
            } catch (t: Throwable) {
                if (!shouldRetry(t)) throw t
                if (remain == 0) throw t  // 마지막 시도면 그냥 throw (onFailed는 block 안에서 이미 호출됨)
                delay(delayMs)
                delayMs = (delayMs * backoffFactor).toLong()
            }
        }
        throw IllegalStateException("retry unreachable")
    }
}