package com.team214.nctue4.utility

import android.content.Context
import com.team214.nctue4.model.*

interface OldE3Interface {
    enum class Status {
        SUCCESS, SERVICE_ERROR, WRONG_CREDENTIALS
    }

    fun getLoginTicket(completionHandler: (status: OldE3Interface.Status,
                                           response: Pair<String, String>?) -> Unit)

    fun getCourseList(completionHandler: (status: OldE3Interface.Status,
                                          response: ArrayList<CourseItem>?) -> Unit)

    fun getAnnouncementDetail(bulletinId: String, from: Int?, courseId: String,
                              completionHandler: (status: OldE3Interface.Status,
                                                  response: AnnItem?) -> Unit)

    fun getAnnouncementListLogin(completionHandler: (status: OldE3Interface.Status,
                                                     response: ArrayList<AnnItem>?) -> Unit)

    fun getCourseAnn(courseId: String, courseName: String,
                     completionHandler: (status: OldE3Interface.Status,
                                         response: ArrayList<AnnItem>?) -> Unit)

    fun getMaterialDocList(courseId: String, context: Context,
                           completionHandler: (status: Status,
                                               response: ArrayList<DocGroupItem>?) -> Unit)

    fun getAttachFileList(documentId: String, courseId: String,
                          completionHandler: (status: Status,
                                              response: ArrayList<AttachItem>?) -> Unit)

    fun cancelPendingRequests()

    fun getCredential() : Pair<String,String>
}