package com.example.codytseng.nctue4.utility

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.codytseng.nctue4.model.*
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import org.json.JSONObject


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
                (0 until annData.length()).map { annData.get(it) as JSONObject }
                        .forEach {
                            annItems.add(AnnItem(
                                    it.getString("BulType").toInt(),
                                    it.getString("BulletinId"),
                                    it.getString("CourseName"),
                                    it.getString("Caption"),
                                    it.getString("Content"),
                                    it.getString("BeginDate"),
                                    it.getString("EndDate"),
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
                (0 until data.length()).map { data.get(it) as JSONObject }
                        .forEach {
                            annItems.add(AnnItem(
                                    it.getInt("BulType"),
                                    it.getString("BulletinId"),
                                    courseName,
                                    it.getString("Caption"),
                                    htmlCleaner(it.getString("Content")),
                                    it.getString("BeginDate"),
                                    it.getString("EndDate"),
                                    ArrayList()
                            ))
                        }
                completionHandler(status, annItems)
            } else {
                completionHandler(status, null)
            }
        }
    }

    override fun getMaterialDocList(courseId: String, docType: String,
                                    completionHandler: (status: OldE3Interface.Status,
                                                        response: ArrayList<DocGroupItem>?) -> Unit) {
        post("/GetMaterialDocList", hashMapOf(
                "loginTicket" to loginTicket,
                "courseId" to courseId,
                "docType" to docType
        )) { status, response ->
            if (status == OldE3Interface.Status.SUCCESS) {
                val arrayOfMaterialDocData = response!!.getJSONObject("ArrayOfMaterialDocData")
                val data = arrayOfMaterialDocData.forceGetJsonArray("MaterialDocData")
                val docGroupItems = ArrayList<DocGroupItem>()
                (0 until data.length()).map { data.get(it) as JSONObject }
                        .forEach {
                            docGroupItems.add(DocGroupItem(
                                    it.getString("DisplayName"),
                                    it.getString("DocumentId"),
                                    it.getString("CourseId")))
                        }
                completionHandler(status, docGroupItems)
            } else {
                completionHandler(status, null)
            }
        }
    }

    override fun getAnnouncementDetail(bulletinId: String,
                                       completionHandler: (status: OldE3Interface.Status,
                                                           response: AnnItem?) -> Unit) {
        post("/GetAnnouncementList_LoginByCountWithAttach", hashMapOf(
                "loginTicket" to loginTicket,
                "studentId" to accountId,
                "ShowCount" to "100"
        )) { status, response ->
            if (status == OldE3Interface.Status.SUCCESS) {
                val data = response!!.getJSONObject("ArrayOfBulletinData").
                        forceGetJsonArray("BulletinData")
                (0 until data.length()).map { data.getJSONObject(it) }
                        .forEach {
                            if (it.getString("BulletinId") == bulletinId) {
                                var attachItemList = ArrayList<AttachItem>()
                                var attachNames = it.forceGetJsonArray("AttachFileName")
                                var attachUrls = it.forceGetJsonArray("AttachFileURL")
                                var attachFileSizes = it.forceGetJsonArray("AttachFileFileSize")
                                if ((attachNames.get(0) as JSONObject).getString("string") != "") {
                                    (0 until attachNames.length()).map {
                                        AttachItem(
                                                (attachNames.get(it) as JSONObject).getString("string"),
                                                (attachFileSizes.get(it) as JSONObject).getString("string"),
                                                (attachUrls.get(it) as JSONObject).getString("string"))
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
                                        it.getString("BeginDate"),
                                        it.getString("EndDate"),
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
                                                       response: ArrayList<DocItem>?) -> Unit) {
        post("/GetAttachFileList", hashMapOf(
                "loginTicket" to loginTicket,
                "resId" to documentId,
                "metaType" to "10", //No idea what is this for
                "courseId" to courseId
        )) { status, response ->
            if (status == OldE3Interface.Status.SUCCESS) {
                val data = response!!.getJSONObject("ArrayOfAttachFileInfoData")
                        .forceGetJsonArray("AttachFileInfoData")
                val docItems = ArrayList<DocItem>()
                (0 until data.length()).map { data.get(it) as JSONObject }
                        .forEach {
                            docItems.add(DocItem(
                                    it.getString("DisplayFileName"),
                                    it.getString("RealityFileName")))
                        }
                completionHandler(status, docItems)
            } else {
                completionHandler(status, null)
            }
        }
    }

    override fun cancelPendingRequests() {
        VolleyHandler.instance?.cancelPendingRequests(tag)
    }
}

