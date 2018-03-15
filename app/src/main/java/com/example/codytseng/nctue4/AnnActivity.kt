package com.example.codytseng.nctue4

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.util.Log
import android.view.View
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.activity_ann.*
import kotlinx.android.synthetic.main.home_announcement_card.*
import kotlinx.android.synthetic.main.fragment_course_ann.*
import org.json.JSONArray

/**
 * Created by s094392 on 3/14/18.
 */
class AnnActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ann)
        var intent =  intent.extras
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val studentId = prefs.getString("studentId", "")
        val studentPassword = prefs.getString("studentPassword", "")
        val courseName = intent.getString("courseName")
        val annId = intent.getString("annId")
        Log.d("arguments", annId)
        val service = OldE3Connect()
        service.getLoginTicket(studentId, studentPassword) { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    service.getAnnouncementDetail(annId) { status, response ->
                        when (status) {
                            OldE3Interface.Status.SUCCESS -> {
                                var attachFile = hashMapOf<String, String>()

                                announctment_caption.text = response!!.getString("Caption")
                                announctment_courseName.text = courseName
                                announctment_date.text = response!!.getString("BeginDate")
                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                                    announctment_content.text = Html.fromHtml(response!!.getString("Content")).replace("\\n\\n+".toRegex(), "\n\n")
                                } else {
                                    announctment_content.text = Html.fromHtml(response!!.getString("Content"), Html.FROM_HTML_MODE_COMPACT).replace("\\n\\n+".toRegex(), "\n\n")
                                }
                                if (response!!.getJSONObject("AttachFileName").getString("string").length > 0) {
                                    announcement_attach.visibility = View.VISIBLE
//                                    announcement_attach_url.text = response!!.getJSONObject("AttachFileName").getString("string")
                                    announcement_attach_name.text = response!!.getJSONObject("AttachFileName").getString("string")
                                    announcement_attach_fileSize.text = response!!.getJSONObject("AttachFileFileSize").getString("string")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}