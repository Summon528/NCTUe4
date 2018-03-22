package com.team214.nctue4.model

import java.text.DateFormat
import java.util.*

class AnnItem(val bulType: Int,
              val bulletinId: String,
              val courseName: String,
              val caption: String,
              val content: String,
              val beginDate: Date,
              val endDate: Date,
              val courseId: String,
              val attachItems: ArrayList<AttachItem>
)