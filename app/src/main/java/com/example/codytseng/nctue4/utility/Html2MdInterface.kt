package com.example.codytseng.nctue4.utility

import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by CodyTseng on 3/13/2018.
 */


interface Html2MdInterface {
    enum class Status {
        SUCCESS, SERVICE_ERROR, WRONG_CREDENTIALS
    }

    fun getMd(html: String, completionHandler: (status: Status, response: String?) -> Unit)
}