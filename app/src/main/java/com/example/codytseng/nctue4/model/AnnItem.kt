package com.example.codytseng.nctue4.model

class AnnItem(val bulType: Int,
              val bulletinId: String,
              val courseName: String,
              val caption: String,
              val content: String,
              val beginDate: String,
              val endDate: String,
              val courseId: String,
              val attachItems: ArrayList<AttachItem>
)