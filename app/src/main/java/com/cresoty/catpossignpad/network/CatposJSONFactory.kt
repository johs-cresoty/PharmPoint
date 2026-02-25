package com.cresoty.catpossignpad.network

import android.util.Log
import com.cresoty.catpossignpad.network.util.getEncryptionData
import org.json.JSONArray
import org.json.JSONObject

object CatposJSONFactory {
    fun makePointDeltaRequestComplex(
        taxno: String,
        cmptr_name: String,
        pos_ver: String,
        trn_date: String,
        trn_time: String,
        trn_amt: String,
        cst_hp: String,
        pair: Pair<HashMap<String, String>, HashMap<String, String>>
    ) : String {
        val json = JSONObject()
        val log = JSONObject()
        val add = JSONArray()
        val addlog = JSONArray()

        json.put("TAXNO", taxno.getEncryptionData())
        log.put("TAXNO", taxno)
        json.put("CMPTR_NAME", cmptr_name.getEncryptionData())
        log.put("CMPTR_NAME", cmptr_name)
        json.put("POS_VER", pos_ver.getEncryptionData())
        log.put("POS_VER", pos_ver)
        json.put("TRN_DATE", trn_date.getEncryptionData())
        log.put("TRN_DATE", trn_date)
        json.put("TRN_AMT", trn_amt.getEncryptionData())
        log.put("TRN_AMT", trn_amt)
        json.put("CST_HP", cst_hp.getEncryptionData())
        log.put("CST_HP", cst_hp)

        pair.toList().map {
            val temp = JSONObject()
            val templog = JSONObject()

            temp.put("TRN_GUBN", (it["TRN_GUBN"] as String).getEncryptionData())
            templog.put("TRN_GUBN", (it["TRN_GUBN"] as String))
            temp.put("TRN_DATE", (it["TRN_DATE"] as String).getEncryptionData())
            templog.put("TRN_DATE", (it["TRN_DATE"] as String))
            temp.put("TRN_TIME", (it["TRN_TIME"] as String).getEncryptionData())
            templog.put("TRN_TIME", (it["TRN_TIME"] as String))
            temp.put("APP_NUM", (it["APP_NUM"] as String).getEncryptionData())
            templog.put("APP_NUM", (it["APP_NUM"] as String))
            temp.put("TRN_AMT", (it["TRN_AMT"] as String).getEncryptionData())
            templog.put("TRN_AMT", (it["TRN_AMT"] as String))

            add.put(temp)
            addlog.put(templog)
        }

        json.put("ADD", add)
        log.put("ADD", addlog)

        Log.d("@#@#", "makePointDeltaRequestComplex : $log")

        return json.toString()
    }

    fun makePointDeltaRequest(
        taxno: String,
        cmptr_name : String,
        pos_ver : String,
        sle_seq : String,
        trn_date : String,
        cst_hp : String
    ) : String {
        val json = JSONObject()
        val log = JSONObject()

        json.put("TAXNO", taxno.getEncryptionData())
        log.put("TAXNO", taxno)
        json.put("CMPTR_NAME", cmptr_name.getEncryptionData())
        log.put("CMPTR_NAME", cmptr_name)
        json.put("POS_VER", pos_ver.getEncryptionData())
        log.put("POS_VER", pos_ver)
        json.put("SLE_SEQ", sle_seq.getEncryptionData())
        log.put("SLE_SEQ", sle_seq)
        json.put("TRN_DATE", trn_date.getEncryptionData())
        log.put("TRN_DATE", trn_date)
        json.put("CST_HP", cst_hp.getEncryptionData())
        log.put("CST_HP", cst_hp)

        Log.d("@#@#", "makePointDeltaRequest : $log")

        return json.toString()
    }

    fun makePointDeltaRequest(
        taxno: String,
        cmptr_name : String,
        pos_ver : String,
        app_num: String,
        trn_amt: String,
        trn_date: String,
        trn_gubn: String,
        cst_hp: String
    ) : String {
        val json = JSONObject()
        val log = JSONObject()

        json.put("TAXNO", taxno.getEncryptionData())
        log.put("TAXNO", taxno)
        json.put("CMPTR_NAME", cmptr_name.getEncryptionData())
        log.put("CMPTR_NAME", cmptr_name)
        json.put("POS_VER", pos_ver.getEncryptionData())
        log.put("POS_VER", pos_ver)
        json.put("TRN_DATE", trn_date.getEncryptionData())
        log.put("TRN_DATE", trn_date)
        json.put("TRN_GUBN", trn_gubn.getEncryptionData())
        log.put("TRN_GUBN", trn_gubn)
        json.put("TRN_AMT", trn_amt.getEncryptionData())
        log.put("TRN_AMT", trn_amt)
        json.put("APP_NUM", app_num.getEncryptionData())
        log.put("APP_NUM", app_num)
        json.put("CST_HP", cst_hp.getEncryptionData())
        log.put("CST_HP", cst_hp)

        Log.d("@#@#", "makePointDeltaRequest : $log")

        return json.toString()
    }

    fun makeExpectSaveAmountRequestComplex(
        taxno: String,
        cmptr_name: String,
        pos_ver: String,
        pair : Pair<HashMap<String, String>, HashMap<String, String>>
    ) : String {
        val json = JSONObject()
        val log = JSONObject()
        val add = JSONArray()
        val addlog = JSONArray()

        json.put("TAXNO", taxno.getEncryptionData())
        log.put("TAXNO", taxno)
        json.put("CMPTR_NAME", cmptr_name.getEncryptionData())
        log.put("CMPTR_NAME", cmptr_name)
        json.put("POS_VER", pos_ver.getEncryptionData())
        log.put("POS_VER", pos_ver)

        pair.toList().map { item ->
            val ob = JSONObject()
            val oblog = JSONObject()

            ob.put("TRN_GUBN", (item["TRN_GUBN"] as String).getEncryptionData())
            oblog.put("TRN_GUBN", item["TRN_GUBN"] as String)
            ob.put("TRN_DATE", (item["TRN_DATE"] as String).getEncryptionData())
            oblog.put("TRN_DATE", item["TRN_DATE"] as String)
            ob.put("TRN_TIME", (item["TRN_TIME"] as String).getEncryptionData())
            oblog.put("TRN_TIME", item["TRN_TIME"] as String)
            ob.put("APP_NUM", (item["APP_NUM"] as String).getEncryptionData())
            oblog.put("APP_NUM", item["APP_NUM"] as String)
            ob.put("TRN_AMT", (item["TRN_AMT"] as String).getEncryptionData())
            oblog.put("TRN_AMT", item["TRN_AMT"] as String)

            add.put(ob)
            addlog.put(oblog)
        }

        json.put("ADD", add)
        log.put("ADD", addlog)

        Log.d("@#@#", "makeExpectSaveAmountRequestComplex : $log")

        return json.toString()
    }

    // 포인트 적립할때 불러오는 데이터
    fun makeExpectSaveAmountRequest(
        taxno : String,
        cmptr_name : String,
        pos_ver : String,
        trn_date : String,
        trn_gubn : String,
        trn_amt : String,
        app_num : String
    ) : String {

        val json = JSONObject()
        val log = JSONObject()

        json.put("TAXNO", taxno.getEncryptionData())
        log.put("TAXNO", taxno)
        json.put("CMPTR_NAME", cmptr_name.getEncryptionData())
        log.put("CMPTR_NAME", cmptr_name)
        json.put("POS_VER", pos_ver.getEncryptionData())
        log.put("POS_VER", pos_ver)
        json.put("TRN_DATE", trn_date.getEncryptionData())
        log.put("TRN_DATE", trn_date)
        json.put("TRN_GUBN", trn_gubn.getEncryptionData())
        log.put("TRN_GUBN", trn_gubn)
        json.put("TRN_AMT", trn_amt.getEncryptionData())
        log.put("TRN_AMT", trn_amt)
        json.put("APP_NUM", app_num.getEncryptionData())
        log.put("APP_NUM", app_num)

        Log.d("@#@#", "makeSaveAmountCheckRequest : $log")

        return json.toString()
    }

}
