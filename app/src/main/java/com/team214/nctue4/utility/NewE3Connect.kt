package com.team214.nctue4.utility

import android.util.Log
import com.android.volley.toolbox.StringRequest
import com.team214.nctue4.model.AnnItem
import org.jsoup.Jsoup
import kotlin.collections.ArrayList
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.*
import com.android.volley.AuthFailureError
import kotlin.collections.HashMap


class NewE3Connect(private var studentId: String = "0616074",
                   private var studentPassword: String = "s0943920224"
                   ) : NewE3Interface {

    private val tag = NewE3Connect::class.java.simpleName

    private fun post(cookie: String, path: String, params: HashMap<String, String>,
                     secondTry: Boolean = false,
                     completionHandler: (status: NewE3Interface.Status,
                                         response: String?, cookie: String) -> Unit) {
        var mCookie = ""
        val url = "https://e3new.nctu.edu.tw$path"
        val stringRequest = object : StringRequest(Request.Method.POST, url,
                Response.Listener<String> { response ->
                    completionHandler(NewE3Interface.Status.SUCCESS, response, mCookie)
                },
                Response.ErrorListener { _ ->
                    completionHandler(NewE3Interface.Status.SERVICE_ERROR, null, mCookie)
                }) {

            override fun getHeaders(): Map<String, String> {
                val localHashMap = HashMap<String,String>()
                localHashMap.put("Cookie", cookie)//向请求头部添加Cookie-本地得到cookie
                Log.d("header", localHashMap.toString())
                return localHashMap
            }

            override fun parseNetworkResponse(
                    response: NetworkResponse): Response<String> {
                    val responseHeaders = response.headers
                    Log.d("key",response.headers.keys.toString())
                    if (responseHeaders.containsKey("Set-Cookie")){
                        mCookie = responseHeaders["Set-Cookie"]!!//此处获取到Cookie，可以保存到缓存中下次使用
                        Log.d("cookie: ", cookie)
                    }
                    val dataString = String(response.data)
                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response))
            }

            override fun getParams(): Map<String, String> {
                return params
            }
        }
        VolleyHandler.instance?.addToRequestQueue(stringRequest, tag)
    }

    override fun getCookie(completionHandler: (status: NewE3Interface.Status, response: String?) -> Unit) {
        post("", "/login/index.php", hashMapOf(
                "username" to studentId,
                "password" to studentPassword
        )) { status, response, cookie ->
            if (status == NewE3Interface.Status.SUCCESS) {
                Log.d("get", cookie)
                completionHandler(NewE3Interface.Status.SUCCESS, cookie)
            } else {
                completionHandler(status, null)
            }
        }
    }

    override fun getAnn(cookie: String, completionHandler: (status: NewE3Interface.Status, response: ArrayList<AnnItem>?) -> Unit) {
        Log.d("yescookie", cookie)
        post(cookie, "/my/", params = HashMap()
        ) { status, response, cookie ->
            if (status == NewE3Interface.Status.SUCCESS) {
                Log.d("getmy", response)
                val annPage = Jsoup.parse(response).select("#pc-for-in-progress div")
                Log.d("getan", annPage.toString())
                for (i in annPage) {
                    Log.d("text", i.toString())
                }
                completionHandler(NewE3Interface.Status.WRONG_CREDENTIALS, null)
            }
        }
    }

    override fun cancelPendingRequests() {
        VolleyHandler.instance?.cancelPendingRequests(tag)
    }
}

