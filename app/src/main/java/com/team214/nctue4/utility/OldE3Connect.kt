package com.team214.nctue4.utility

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.team214.nctue4.R
import com.team214.nctue4.model.AnnItem
import com.team214.nctue4.model.AttachItem
import com.team214.nctue4.model.CourseItem
import com.team214.nctue4.model.DocGroupItem
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class OldE3Connect(private var studentId: String = "",
                   private var studentPassword: String = "",
                   private var loginTicket: String = "",
                   private var accountId: String = "") : OldE3Interface {

    private val tag = OldE3Connect::class.java.simpleName

    override fun getCredential() = Pair(loginTicket, accountId)

    private fun post(path: String, params: HashMap<String, String>,
                     secondTry: Boolean = false,
                     completionHandler: (status: OldE3Interface.Status,
                                         response: JSONObject?) -> Unit) {
        val url = "http://e3.nctu.edu.tw/mService/Service.asmx$path"
        val stringRequest = object : StringRequest(Request.Method.POST, url,
                Response.Listener<String> { response ->
                    val xmlToJson = (XmlToJson.Builder(response).build()).toJson()
                    completionHandler(OldE3Interface.Status.SUCCESS, xmlToJson)
                },
                Response.ErrorListener { _ ->
                    if (!secondTry) {
                        getLoginTicket { _, _ ->
                            post(path, params, true, completionHandler)
                        }
                    }
                    completionHandler(OldE3Interface.Status.SERVICE_ERROR, null)
                }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }
        VolleyHandler.instance?.addToRequestQueue(stringRequest, tag)
    }


    override fun getLoginTicket(completionHandler: (status: OldE3Interface.Status,
                                                    response: Pair<String, String>?) -> Unit) {
        post("/Login", hashMapOf(
                "account" to studentId,
                "password" to studentPassword
        )) { status, response ->
            if (status == OldE3Interface.Status.SUCCESS) {
                val accountData = response!!.getJSONObject("AccountData")
                if (accountData.has("LoginTicket")) {
                    val studentName = accountData.getString("Name")
                    val studentEmail = accountData.getString("EMail")
                    loginTicket = accountData.getString("LoginTicket")
                    accountId = accountData.getString("AccountId")
                    completionHandler(OldE3Interface.Status.SUCCESS, Pair(studentName, studentEmail))
                } else {
                    completionHandler(OldE3Interface.Status.WRONG_CREDENTIALS, null)
                }
            } else {
                completionHandler(status, null)
            }
        }
    }

    override fun getCourseList(completionHandler: (status: OldE3Interface.Status,
                                                   response: ArrayList<CourseItem>?) -> Unit) {
        post("/GetCourseList", hashMapOf(
                "loginTicket" to loginTicket,
                "accountId" to accountId,
                "role" to "stu"
        )) { status, response ->
            if (status == OldE3Interface.Status.SUCCESS) {
                val data = response!!.getJSONObject("ArrayOfCourseData")
                        .forceGetJsonArray("CourseData")
                val courseItems = ArrayList<CourseItem>()
                (0 until data.length()).map { data.get(it) as JSONObject }
                        .forEach {
                            courseItems.add(CourseItem(it.getInt("CourseNo"),
                                    it.getString("CourseName"),
                                    it.getString("TeacherName"),
                                    it.getString("CourseId")))
                        }
                completionHandler(status, courseItems)
            } else {
                completionHandler(status, null)
            }
        }
    }

    override fun getAnnouncementListLogin(completionHandler: (status: OldE3Interface.Status,
                                                              response: ArrayList<AnnItem>?) -> Unit) {
        post("/GetAnnouncementList_LoginByCountWithAttach", hashMapOf(
                "loginTicket" to loginTicket,
                "studentId" to accountId,
                "ShowCount" to "100"
        )) { status, response ->
            if (status == OldE3Interface.Status.SUCCESS) {
                val annData = response!!.getJSONObject("ArrayOfBulletinData")
                        .forceGetJsonArray("BulletinData")
                val annItems = ArrayList<AnnItem>()
                val df = SimpleDateFormat("yyyy/M/d", Locale.US)
                (0 until annData.length()).map { annData.get(it) as JSONObject }
                        .forEach {
                            annItems.add(AnnItem(
                                    it.getString("BulType").toInt(),
                                    it.getString("BulletinId"),
                                    it.getString("CourseName"),
                                    it.getString("Caption"),
                                    it.getString("Content"),
                                    df.parse(it.getString("BeginDate")),
                                    df.parse(it.getString("EndDate")),
                                    it.getString("CourseId"),
                                    ArrayList()
                            ))
                        }
                completionHandler(status, annItems)
            } else {
                completionHandler(status, null)
            }
        }
    }

    override fun getCourseAnn(courseId: String, courseName: String,
                              completionHandler: (status: OldE3Interface.Status,
                                                  response: ArrayList<AnnItem>?) -> Unit) {
        post("/GetAnnouncementList", hashMapOf(
                "loginTicket" to loginTicket,
                "courseId" to courseId,
                "bulType" to "1"
        )) { status, response ->
            if (status == OldE3Interface.Status.SUCCESS) {
                val arrayOfBulletinData = response!!.getJSONObject("ArrayOfBulletinData")
                val data = arrayOfBulletinData.forceGetJsonArray("BulletinData")
                val annItems = ArrayList<AnnItem>()
                val df = SimpleDateFormat("yyyy/M/d", Locale.US)
                (0 until data.length()).map { data.get(it) as JSONObject }
                        .forEach {
                            annItems.add(AnnItem(
                                    it.getInt("BulType"),
                                    it.getString("BulletinId"),
                                    courseName,
                                    it.getString("Caption"),
                                    htmlCleaner(it.getString("Content")),
                                    df.parse(it.getString("BeginDate")),
                                    df.parse(it.getString("EndDate")),
                                    it.getString("CourseId"),
                                    ArrayList()
                            ))
                        }
                completionHandler(status, annItems)
            } else {
                completionHandler(status, null)
            }
        }
    }


    private lateinit var getMaterialDocListStatus: Array<Boolean>
    private var docGroupItems: ArrayList<DocGroupItem>? = null
    override fun getMaterialDocList(courseId: String, context: Context,
                                    completionHandler: (status: OldE3Interface.Status,
                                                        response: ArrayList<DocGroupItem>?) -> Unit) {
        docGroupItems = ArrayList()
        getMaterialDocListStatus = Array(2, { false })
        for (i in 0..1) {
            post("/GetMaterialDocList", hashMapOf(
                    "loginTicket" to loginTicket,
                    "courseId" to courseId,
                    "docType" to i.toString()
            )) { status, response ->
                if (status == OldE3Interface.Status.SUCCESS) {
                    processMaterialDocList(i, response!!, context, completionHandler)
                } else {
                    completionHandler(status, null)
                }
            }
        }
    }


    private fun processMaterialDocList(which: Int, response: JSONObject, context: Context,
                                       completionHandler: (status: OldE3Interface.Status,
                                                           response: ArrayList<DocGroupItem>?) -> Unit) {

        val arrayOfMaterialDocData = response.getJSONObject("ArrayOfMaterialDocData")
        val data = arrayOfMaterialDocData.forceGetJsonArray("MaterialDocData")
        Log.d("RESP",response.toString())
        (0 until data.length()).map { data.get(it) as JSONObject }
                .forEach {
                    var dateArray: List<String> = it.getString("BeginDate").split("/")
                    docGroupItems!!.add(DocGroupItem(
                            it.getString("DisplayName"),
                            it.getString("DocumentId"),
                            it.getString("CourseId"),
                            if (which == 0) context.getString(R.string.course_doc_type_handout)
                            else context.getString(R.string.course_doc_type_reference)
                    ))
                }
        getMaterialDocListStatus[which] = true
        if (getMaterialDocListStatus[0] && getMaterialDocListStatus[1]) {
            docGroupItems?.sortByDescending { it.docType }
            completionHandler(OldE3Interface.Status.SUCCESS, docGroupItems)
            docGroupItems = null
        }
    }


    override fun getAnnouncementDetail(bulletinId: String, from: Int?, courseId: String,
                                       completionHandler: (status: OldE3Interface.Status,
                                                           response: AnnItem?) -> Unit) {
        Log.d("ann", loginTicket)
        val path = if (from == OldE3AnnFrom.HOME) "/GetAnnouncementList_LoginByCountWithAttach"
        else "/GetAnnouncementListWithAttach"
        post(path, hashMapOf(
                "loginTicket" to loginTicket,
                "studentId" to accountId,
                "ShowCount" to "100",
                "courseId" to courseId,
                "bulType" to "1"
        )) { status, response ->
            if (status == OldE3Interface.Status.SUCCESS) {
                val data = response!!.getJSONObject("ArrayOfBulletinData").forceGetJsonArray("BulletinData")
                val df = SimpleDateFormat("yyyy/M/d", Locale.US)
                (0 until data.length()).map { data.getJSONObject(it) }
                        .forEach {
                            if (it.getString("BulletinId") == bulletinId) {
                                val attachItemList = ArrayList<AttachItem>()
                                val attachNames = it.forceGetJsonArray("AttachFileName")
                                val attachUrls = it.forceGetJsonArray("AttachFileURL")
                                val attachFileSizes = it.forceGetJsonArray("AttachFileFileSize")
                                if ((attachNames.get(0) as JSONObject).getString("string") != "") {
                                    (0 until attachNames.length()).map {
                                        AttachItem(
                                                (attachNames.get(it) as JSONObject).getString("string").dropLast(1),
                                                (attachFileSizes.get(it) as JSONObject).getString("string").dropLast(1),
                                                (attachUrls.get(it) as JSONObject).getString("string").dropLast(1))
                                    }.forEach {
                                        attachItemList.add(it)
                                    }
                                }
                                val annItem = AnnItem(
                                        it.getInt("BulType"),
                                        it.getString("BulletinId"),
                                        it.getString("CourseName"),
                                        it.getString("Caption"),
                                        htmlCleaner(it.getString("Content")),
                                        df.parse(it.getString("BeginDate")),
                                        df.parse(it.getString("EndDate")),
                                        it.getString("CourseId"),
                                        attachItemList
                                )
                                completionHandler(status, annItem)
                            }
                        }
            } else {
                completionHandler(status, null)
            }
        }
    }

    override fun getAttachFileList(documentId: String, courseId: String,
                                   completionHandler: (status: OldE3Interface.Status,
                                                       response: ArrayList<AttachItem>?) -> Unit) {
        post("/GetAttachFileList", hashMapOf(
                "loginTicket" to loginTicket,
                "resId" to documentId,
                "metaType" to "10", //No idea what is this for
                "courseId" to courseId
        )) { status, response ->
            if (status == OldE3Interface.Status.SUCCESS) {
                val data = response!!.getJSONObject("ArrayOfAttachFileInfoData")
                        .forceGetJsonArray("AttachFileInfoData")
                val attachItems = ArrayList<AttachItem>()
                (0 until data.length()).map { data.get(it) as JSONObject }
                        .forEach {
                            attachItems.add(AttachItem(
                                    it.getString("DisplayFileName"),
                                    it.getString("FileSize"),
                                    it.getString("RealityFileName")))
                        }
                completionHandler(status, attachItems)
            } else {
                completionHandler(status, null)
            }
        }
    }

    override fun cancelPendingRequests() {
        VolleyHandler.instance?.cancelPendingRequests(tag)
    }
}

