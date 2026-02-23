package com.cresoty.catpossignpad.network

import com.cresoty.catpossignpad.network.util.CatposCloudDecryptor
import com.google.gson.annotations.JsonAdapter

typealias CatposSaveAmountResponse = CatposResponse<InfoResponse<CatposCheckDeltaAmount>>
typealias CatposPointDeltaResponse = CatposResponse<InfoResponse<CatposPointDelta>>
typealias CatposBalanceResponse = CatposResponse<InfoResponse<CatposCheckBalance>>
typealias CatposSaveSettingResponse = CatposResponse<InfoResponse<CatposSaveSetting>>
typealias CatposAmountSettingResponse = CatposResponse<InfoResponse<CatposAmountSetting>>

data class CatposResponse<T>(
    val MSG: String,
    val CODE: String,
    val DATA: T,
    val DTL: String?,
)

data class InfoResponse<T>(
    val INFO : List<T>?
)

data class ListResponse<T>(
    val LIST : List<T>?
)

// SUM 데이터 없는 경우 처리를 위해 사용 하는 빈 클래스
//class NoSUM

//data class CatposTransResultResponse(
//    val INFO : List<CatposTransResultInfo>,
//    val LIST : List<CatposTransResultList>
//)

data class CatposCheckDeltaAmount(
    @JsonAdapter(CatposCloudDecryptor::class) val SLE_SEQ: String,
    @JsonAdapter(CatposCloudDecryptor::class) val GRD_CODE : String,
    @JsonAdapter(CatposCloudDecryptor::class) val PNT_AMT : String,
    @JsonAdapter(CatposCloudDecryptor::class) val PAY_PNT_AMT : String,
    @JsonAdapter(CatposCloudDecryptor::class) val PNT_BLC : String,
    @JsonAdapter(CatposCloudDecryptor::class) val CST_CODE : String
)

data class CatposPointDelta(
    @JsonAdapter(CatposCloudDecryptor::class) val SLE_SEQ : String,
    @JsonAdapter(CatposCloudDecryptor::class) val CST_CODE : String,
    @JsonAdapter(CatposCloudDecryptor::class) val CST_HP : String,
    @JsonAdapter(CatposCloudDecryptor::class) val CST_NAME : String,
    @JsonAdapter(CatposCloudDecryptor::class) val PNT_AMT : String,
    @JsonAdapter(CatposCloudDecryptor::class) val PNT_BLC : String
)

data class CatposCheckBalance(
    @JsonAdapter(CatposCloudDecryptor::class) val CST_GNDR : String,
    @JsonAdapter(CatposCloudDecryptor::class) val PNT_BLC: String,
    @JsonAdapter(CatposCloudDecryptor::class) val CST_BRTH : String,
    @JsonAdapter(CatposCloudDecryptor::class) val CST_NAME : String,
    @JsonAdapter(CatposCloudDecryptor::class) val CST_CODE : String,
    @JsonAdapter(CatposCloudDecryptor::class) val CST_HP: String
)

data class CatposSaveSetting(
    @JsonAdapter(CatposCloudDecryptor::class) val TAXNO : String,           // 사업자번호
    @JsonAdapter(CatposCloudDecryptor::class) val PNT_GUBN : String,        // 적립방법 : CST(고객) / SLE(판매) / NON(적입안함)
    @JsonAdapter(CatposCloudDecryptor::class) val GDS_YN : String,          // 상품별 적립 여부 : Y/N
    @JsonAdapter(CatposCloudDecryptor::class) val PAY_YN : String,          // 결제급액별 적립 여부 : Y/N
    @JsonAdapter(CatposCloudDecryptor::class) val RND_RULE : String,        // 적립한도(optional)
    @JsonAdapter(CatposCloudDecryptor::class) val CAP_PNT : String,         // 반올림 규칙 : T(TRUNC자름?) / U(올림) / D(내림) / R(반올림)
    @JsonAdapter(CatposCloudDecryptor::class) val CMPTR_NAME : String,      // 컴퓨터 명
    @JsonAdapter(CatposCloudDecryptor::class) val USR_ID : String,          // 사용자 아이디
    @JsonAdapter(CatposCloudDecryptor::class) val RDT : String,             // 등록일시
    @JsonAdapter(CatposCloudDecryptor::class) val UDT : String              // 수정일시
)

data class CatposAmountSetting(
    @JsonAdapter(CatposCloudDecryptor::class) val CAP_PNT : String,         // 최대적립한도
    @JsonAdapter(CatposCloudDecryptor::class) val BASE_AMT : String,        // 기준금액(초과)
    @JsonAdapter(CatposCloudDecryptor::class) val CMPTR_NAME : String,      // 컴퓨터명
    @JsonAdapter(CatposCloudDecryptor::class) val CASH_PNT_VAL : String,    // 현금 적립값
    @JsonAdapter(CatposCloudDecryptor::class) val CARD_PNT_VAL : String,    // 카드 적립값
    @JsonAdapter(CatposCloudDecryptor::class) val PNT_TYPE : String,        // 포인트유형 : R(비율) / A(금액)
    @JsonAdapter(CatposCloudDecryptor::class) val USR_ID : String,          // 유저아이디
    @JsonAdapter(CatposCloudDecryptor::class) val SEQ : String,             // 일련번호
    @JsonAdapter(CatposCloudDecryptor::class) val SDATE : String,           // 유효시작일
    @JsonAdapter(CatposCloudDecryptor::class) val EDATE : String,           // 유효종료일
    @JsonAdapter(CatposCloudDecryptor::class) val UDT : String,
    @JsonAdapter(CatposCloudDecryptor::class) val RDT : String,
)