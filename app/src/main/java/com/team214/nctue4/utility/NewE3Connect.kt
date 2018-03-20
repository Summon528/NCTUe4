package com.team214.nctue4.utility

import android.util.Log
import com.team214.nctue4.model.AnnItem
import org.jsoup.Jsoup
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import okhttp3.*
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class NewE3Connect(private var studentId: String = "",
                   private var studentPassword: String = ""
                   ) : NewE3Interface {

    private val tag = NewE3Connect::class.java.simpleName

    private fun post(cookie: String, path: String, params: HashMap<String, String>,
                     secondTry: Boolean = false,
                     completionHandler: (status: NewE3Interface.Status,
                                         response: String?, cookie: String) -> Unit) {
        val client = OkHttpClient().newBuilder().followRedirects(false).followSslRedirects(false).build()
        val url = "https://e3new.nctu.edu.tw$path"

        val formBody = FormBody.Builder().add("username", studentId).add("password", studentPassword).build()
        val request = Request.Builder().url(url).post(formBody).build()

        val call = client.newCall(request)
        call.enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException){
                completionHandler(NewE3Interface.Status.SERVICE_ERROR, null, "")
            }
            override fun onResponse(call: Call, response: Response){
                Log.d("content", response.toString())
                val mCookie = response.headers().toMultimap()["Set-Cookie"]!!
                if (mCookie.size > 1){
                    Log.d("cookies", mCookie.toString())
                    completionHandler(NewE3Interface.Status.SUCCESS, response.body().string(), mCookie[1])
                }
                completionHandler(NewE3Interface.Status.SUCCESS, response.body().string(), "")
            }
        })
    }

    override fun getCookie(completionHandler: (status: NewE3Interface.Status, response: String?) -> Unit) {
        post("", "/login/index.php", hashMapOf(
                "username" to "0616074",
                "password" to "s0943924",
                "rememberusername" to "1"
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
        post(cookie, "/my/", HashMap()
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

