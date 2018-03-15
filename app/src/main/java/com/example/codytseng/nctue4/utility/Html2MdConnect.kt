package com.example.codytseng.nctue4.utility

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import fr.arnaudguyon.xmltojsonlib.XmlToJson

import org.json.JSONObject

class Html2MdConnect : Html2MdInterface {
    private val tag = Html2MdConnect::class.java.simpleName

    private fun post(path: String, params: HashMap<String, String>,
                     completionHandler: (status: Html2MdInterface.Status, response: String?) -> Unit) {
        val url = "http://fuckyeahmarkdown.com${path}"
        val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener<String> { response ->
                    completionHandler(Html2MdInterface.Status.SUCCESS, response)
                },
                Response.ErrorListener { response ->
                    completionHandler(Html2MdInterface.Status.SERVICE_ERROR, null)
                }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }
        VolleyHandler.instance?.addToRequestQueue(stringRequest, tag)
    }


    override fun getMd(html: String, completionHandler: (status: Html2MdInterface.Status, response: String?) -> Unit) {
        val params = HashMap<String, String>()
        params.put("html", html)
        post("/go/", params) { status, response ->
            if (status == Html2MdInterface.Status.SUCCESS) {
                completionHandler(status, response!!.toString())
            } else {
                completionHandler(status, "")
            }
        }
    }

}