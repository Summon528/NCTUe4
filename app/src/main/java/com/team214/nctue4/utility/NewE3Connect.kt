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
import okhttp3.Cookie
import java.net.URI


class NewE3Connect(private var studentId: String = "",
                   private var studentPassword: String = "",
                   private var newE3Cookie: String = "") : NewE3Interface {



    private val tag = NewE3Connect::class.java.simpleName
    private var cookieStore: HashMap<String, MutableList<Cookie>> = HashMap()
    private fun post(path: String, params: HashMap<String, String>,
                     secondTry: Boolean = false,
                     completionHandler: (status: NewE3Interface.Status, cookie: String?,
                                         response: String?) -> Unit) {
        val client = OkHttpClient().newBuilder().cookieJar(object: CookieJar{
            override fun loadForRequest(url: HttpUrl?): MutableList<Cookie>? {
                Log.d("send cookie", newE3Cookie)
                if (cookieStore[url!!.host()] != null) {
                    newE3Cookie = cookieStore[url!!.host()]!![0].value()
                    return cookieStore[url!!.host()]
                } else {
                    if(newE3Cookie!=""){
                        val tmp = emptyList<Cookie>().toMutableList()
                        tmp.add(Cookie.parse(url, "MoodleSession=" + newE3Cookie))
                        return tmp
                    }
                    else
                        return emptyList<Cookie>().toMutableList()
                }
            }
            override fun saveFromResponse(url: HttpUrl?, cookies: MutableList<Cookie>?) {
                if (cookies!!.size>1)
                    cookieStore[url!!.host()] = cookies.subList(1, 2)
                else
                    cookieStore[url!!.host()] = cookies
                Log.d("saved cookie", cookieStore[url!!.host()].toString())
            }
        }).build()
        val url = "https://e3new.nctu.edu.tw$path"
        Log.d("user an pas", studentId + " " + studentPassword)
        val formBody = FormBody.Builder().add("username", studentId).add("password", studentPassword).build()
        val request = Request.Builder().url(url).post(formBody).build()

        val call = client.newCall(request)
        call.enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException){
                if (!secondTry) {
                    getCookie { _, _ ->
                        post(path, params, true, completionHandler)
                    }
                }
                completionHandler(NewE3Interface.Status.SERVICE_ERROR, null, null)
            }
            override fun onResponse(call: Call, response: Response){
                val res = response.body().string()
                if (res.contains("New E3 數位教學平台: 登入本網站")){
                    Log.d("fail", cookieStore.toString())
                    if (!secondTry) {
                        getCookie { _, _ ->
                            post(path, params, true, completionHandler)
                        }
                    }
                    completionHandler(NewE3Interface.Status.SERVICE_ERROR, null, null)
                }
                completionHandler(NewE3Interface.Status.SUCCESS, newE3Cookie, res)
            }
        })
    }

    override fun getCookie(completionHandler: (status: NewE3Interface.Status, response: String?) -> Unit) {
        post("/login/index.php", hashMapOf(
                "username" to studentId,
                "password" to studentPassword
        )) { status, cookie, response->
            if (status == NewE3Interface.Status.SUCCESS) {
                completionHandler(NewE3Interface.Status.SUCCESS, cookie)
            } else {
                completionHandler(status, null)
            }
        }
    }

    override fun getAnn(completionHandler: (status: NewE3Interface.Status, response: ArrayList<AnnItem>?) -> Unit) {
        post("/my/", HashMap()
        ) { status, cookie, response->
            if (status == NewE3Interface.Status.SUCCESS) {
                Log.d("getmy", response)
                val annPage = Jsoup.parse(response).select("#pc-for-in-progress div")
                Log.d("getan", annPage.toString())
                for (i in annPage) {
                    Log.d("text", i.toString())
                }
                completionHandler(NewE3Interface.Status.WRONG_CREDENTIALS, null)
            } else {
                completionHandler(status, null)
            }
        }
    }

    override fun cancelPendingRequests() {
        VolleyHandler.instance?.cancelPendingRequests(tag)
    }
}

