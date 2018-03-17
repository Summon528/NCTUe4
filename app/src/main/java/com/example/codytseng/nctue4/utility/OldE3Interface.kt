package com.example.codytseng.nctue4.utility

import com.example.codytseng.nctue4.model.AnnItem
import com.example.codytseng.nctue4.model.CourseItem
import com.example.codytseng.nctue4.model.DocGroupItem
import com.example.codytseng.nctue4.model.DocItem

interface OldE3Interface {
    enum class Status {
        SUCCESS, SERVICE_ERROR, WRONG_CREDENTIALS
    }

    fun getLoginTicket(completionHandler: (status: OldE3Interface.Status,
                                           response: Pair<String, String>?) -> Unit)

    fun getCourseList(completionHandler: (status: OldE3Interface.Status,
                                          response: ArrayList<CourseItem>?) -> Unit)

    fun getAnnouncementDetail(bulletinId: String,
                              completionHandler: (status: OldE3Interface.Status,
                                                  response: AnnItem?) -> Unit)

    fun getAnnouncementListLogin(completionHandler: (status: OldE3Interface.Status,
                                                     response: ArrayList<AnnItem>?) -> Unit)

    fun getCourseAnn(courseId: String, courseName: String,
                     completionHandler: (status: OldE3Interface.Status,
                                         response: ArrayList<AnnItem>?) -> Unit)

    fun getMaterialDocList(courseId: String, docType: String,
                           completionHandler: (status: Status,
                                               response: ArrayList<DocGroupItem>?) -> Unit)

    fun getAttachFileList(documentId: String, courseId: String,
                          completionHandler: (status: Status,
                                              response: ArrayList<DocItem>?) -> Unit)

    fun cancelPendingRequests()

    fun getCredential() : Pair<String,String>
}