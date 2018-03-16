package com.example.codytseng.nctue4.utility

import android.content.Context
import com.example.codytseng.nctue4.model.DocItem
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by CodyTseng on 3/13/2018.
 */


interface OldE3Interface {
    enum class Status {
        SUCCESS, SERVICE_ERROR, WRONG_CREDENTIALS
    }

    fun getLoginTicket(studentId: String, studentPassword: String,
                       completionHandler: (status: OldE3Interface.Status,
                                           response: Pair<String, String>?) -> Unit)

    fun getCourseList(completionHandler: (status: OldE3Interface.Status, response: JSONArray?) -> Unit)

    fun getAnnouncementDetail(bulletinId: String, completionHandler: (status: OldE3Interface.Status, response: JSONObject?) -> Unit)

    fun getAnnouncementList_Login(completionHandler: (status: OldE3Interface.Status, response: JSONArray?) -> Unit)

    fun getCourseAnn(courseId : String, completionHandler: (status: Status, response: JSONArray?) -> Unit)

    fun getMaterialDocList(courseId :String, docType:String, completionHandler: (status: Status, response: JSONArray?) -> Unit)

    fun getAttachFileList(documentId : String, courseId: String, completionHandler: (status: Status, response: ArrayList<DocItem>?) -> Unit)
}