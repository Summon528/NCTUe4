package com.team214.nctue4.utility

import android.preference.PreferenceManager
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
import org.jsoup.select.Elements
import java.net.URI
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale




class NewE3Connect(private var studentId: String = "",
                   private var studentPassword: String = "",
                   private var newE3Cookie: String = "") : NewE3Interface {

    private val tag = NewE3Connect::class.java.simpleName
    private var cookieStore: HashMap<String, MutableList<Cookie>> = HashMap()

    override fun getCredential() = newE3Cookie

    private fun post(path: String, params: HashMap<String, String>,
                     secondTry: Boolean = false,
                     completionHandler: (status: NewE3Interface.Status, cookie: String?,
                                         response: String?) -> Unit) {
        val client = OkHttpClient().newBuilder().cookieJar(object: CookieJar{
            override fun loadForRequest(url: HttpUrl?): MutableList<Cookie>? {
                Log.d("send cookie", newE3Cookie)
                if (cookieStore[url!!.host()] != null) {
                    newE3Cookie = cookieStore[url.host()]!![0].value()
                }
                if(newE3Cookie!=""){
                    Log.d("has cookie", url.toString())
                    val tmp = emptyList<Cookie>().toMutableList()
                    tmp.add(Cookie.parse(url, "MoodleSession=" + newE3Cookie))
                    return tmp
                }
                else
                    return emptyList<Cookie>().toMutableList()

            }
            override fun saveFromResponse(url: HttpUrl?, cookies: MutableList<Cookie>?) {
                if (cookies!!.size>1)
                    cookieStore[url!!.host()] = cookies.subList(1, 2)
                else
                    cookieStore[url!!.host()] = cookies
                Log.d("saved cookie", cookieStore[url.host()].toString())
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
                    cookieStore.clear()
                    newE3Cookie = ""
                    getCookie { _, _ ->
                        post(path, params, true, completionHandler)
                    }
                }
                completionHandler(NewE3Interface.Status.SERVICE_ERROR, null, null)
            }
            override fun onResponse(call: Call, response: Response){
                val res = response.body().string()
                if (res.contains("Log in to the site")){
                    Log.d("fail", cookieStore.toString())
                    cookieStore.clear()
                    newE3Cookie = ""
                    if (!secondTry) {
                        getCookie { _, _ ->
                            post(path, params, true, completionHandler)
                        }
                    }
                    completionHandler(NewE3Interface.Status.SERVICE_ERROR, null, null)
                }
                if (res.contains("pc-for-in-progress")){
                    Log.d("con", "yes")
                }
                completionHandler(NewE3Interface.Status.SUCCESS, newE3Cookie, res)
            }
        })
    }

    override fun getCookie(completionHandler: (status: NewE3Interface.Status, response: String?) -> Unit) {
        post("/login/index.php?lang=en", hashMapOf(
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
        post("/my/index.php?lang=en", HashMap()
        ) { status, cookie, response->
            if (status == NewE3Interface.Status.SUCCESS) {
                Log.d("loca", response!!)
                if (response.contains("pc-for-in-progress")){
                    Log.d("con", "yes")
                }
                val annPage = Jsoup.parse(response).select("#pc-for-in-progress")[0].select(" .course-info-container .hidden-xs-down")
                var annItems =  ArrayList<AnnItem>()
                val df = SimpleDateFormat("d LLL,  yyyy", Locale.US)
                (0 until annPage.size).map {annPage[it] as org.jsoup.nodes.Element }
                        .forEach{
                            if (it.select("b").text() != "System"){
                                annItems.add(AnnItem(
                                        1,
                                        it.select("a").attr("href").substring(25) + "&lang=en",
                                        it.select("b").text().substring(10).replace(" .*".toRegex(), ""),
                                        it.select("h4").text(),
                                        it.select("a").text(),
                                        df.parse(it.select(".media div")[0].text().substring(0, 20).replace("([0-9]+[\\.|\\:,][0-9]*)".toRegex(), "") + "2018"),
                                        df.parse(it.select(".media div")[0].text().substring(0, 20).replace("([0-9]+[\\.|\\:,][0-9]*)".toRegex(), "") + "2018"),
                                        "",
                                        ArrayList()
                                ))
                            }

                        }
                completionHandler(NewE3Interface.Status.SUCCESS, annItems)
            } else {
                completionHandler(status, null)
            }
        }
    }

    override fun getAnnDetail(bulletinId: String, completionHandler: (status: NewE3Interface.Status, response: AnnItem?) -> Unit) {
        Log.d("buul id", bulletinId)
        post(bulletinId, HashMap()
        ) { status, cookie, response->
//            Log.d("detail cookie", cookie)
            if (status == NewE3Interface.Status.SUCCESS) {
                Log.d("getanndetail", response)
                val annPage = Jsoup.parse(response)
                val df = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.US)
                val caption = if(annPage.select(".name").size > 0){
                    annPage.select(".name").text().substring(5)
                } else {
                    annPage.select(".subject").text().substring(5)
                }
                val annItem = AnnItem(
                        1,
                        bulletinId,
                        annPage.select(".page-header-headings").text().replace("【.*】\\d*".toRegex(), "").replace(" .*".toRegex(), ""),
                        caption,
                        annPage.select(".content").html(),
                        df.parse(annPage.select(".author").text().replace(", \\d+:\\d+.*".toRegex(), "")),
                        df.parse(annPage.select(".author").text().replace(", \\d+:\\d+.*".toRegex(), "")),
                        "",
                        ArrayList()
                )
                completionHandler(NewE3Interface.Status.SUCCESS, annItem)
            } else {
                completionHandler(status, null)
            }
        }
    }

    override fun cancelPendingRequests() {
        VolleyHandler.instance?.cancelPendingRequests(tag)
    }
}

