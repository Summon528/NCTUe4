package com.example.codytseng.nctue4.model

/**
 * Created by s094392 on 3/14/18.
 */
class AttachItem(name: String, fileSize: String, url: String) {
    var mName: String = name
    var mUrl: String = url
    var mFileSize: String = fileSize + " KB"
}