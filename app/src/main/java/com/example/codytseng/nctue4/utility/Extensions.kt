package com.example.codytseng.nctue4.utility

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.example.codytseng.nctue4.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

fun JSONObject.forceGetJsonArray(name: String): JSONArray {
    return try {
        this.getJSONArray(name)
    } catch (e: JSONException) {
        val tmp = JSONArray()
        tmp.put(this.getJSONObject(name))
        tmp
    }
}

