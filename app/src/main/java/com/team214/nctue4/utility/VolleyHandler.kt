package com.team214.nctue4.utility

import android.app.Application
import android.text.TextUtils
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleyHandler : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    private var _requestQueue: RequestQueue? = null

    private val requestQueue: RequestQueue?
        get() {
            if (_requestQueue == null) {
                _requestQueue = Volley.newRequestQueue(applicationContext)
            }
            return _requestQueue
        }

    fun <T> addToRequestQueue(request: Request<T>, tag: String) {
        request.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        requestQueue?.add(request)
    }

    fun <T> addToRequestQueue(request: Request<T>) {
        request.tag = TAG
        requestQueue?.add(request)
    }

    fun cancelPendingRequests(tag: Any) {
        if (requestQueue != null) {
            requestQueue!!.cancelAll(tag)
        }
    }

    companion object {
        private val TAG = VolleyHandler::class.java.simpleName
        @get:Synchronized
        var instance: VolleyHandler? = null
            private set
    }
}