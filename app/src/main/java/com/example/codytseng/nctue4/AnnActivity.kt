package com.example.codytseng.nctue4

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.ScrollView
import com.example.codytseng.nctue4.model.AttachItem
import com.example.codytseng.nctue4.utility.Html2MdConnect
import com.example.codytseng.nctue4.utility.HtmlCleaner
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.activity_ann.*


import org.json.JSONException


/**
 * Created by s094392 on 3/14/18.
 */
class AnnActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ann)
        val intent =  intent.extras
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val studentId = prefs.getString("studentId", "")
        val studentPassword = prefs.getString("studentPassword", "")
        val courseName = intent.getString("courseName")
        val annId = intent.getString("annId")
        val loading = findViewById<ProgressBar>(R.id.loading_spinner)
        val annContent = findViewById<ScrollView>(R.id.ann_content)
        loading.visibility = View.VISIBLE
        annContent.visibility = View.GONE
        Log.d("arguments", annId)
        val service = OldE3Connect()
        service.getLoginTicket(studentId, studentPassword) { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    service.getAnnouncementDetail(annId) { status, response ->
                        when (status) {
                            OldE3Interface.Status.SUCCESS -> {
                                announctment_caption.text = response!!.getString("Caption")
                                announctment_courseName.text = courseName
                                announctment_date.text = response.getString("BeginDate")

                                val Html2Md = Html2MdConnect()
                                val trashHtml: String
                                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                                    trashHtml = Html.fromHtml(HtmlCleaner(response.getString("Content"))).replace("\\n\\n+".toRegex(), "\n\n")
                                } else {
                                    trashHtml = Html.fromHtml(HtmlCleaner(response.getString("Content")), Html.FROM_HTML_MODE_COMPACT).replace("\\n\\n+".toRegex(), "\n\n")
                                }
                                announctment_content.text = trashHtml
//                                Html2Md.getMd(trashHtml) { status, response ->
//                                    when (status) {
//                                        Html2MdInterface.Status.SUCCESS -> {
//                                            announctment_content.loadMarkdown(response!!)
//                                        }
//                                    }
//                                }
                                val attachFileName = response.getJSONObject("AttachFileName")
                                val attachFileUrl = response.getJSONObject("AttachFileURL")
                                val attachFileFileSize = response.getJSONObject("AttachFileFileSize")
                                var attachItems = ArrayList<AttachItem>()
                                try {
                                    val attachFileNames = attachFileName.getJSONArray("string")
                                    val attachFileUrls = attachFileUrl.getJSONArray("string")
                                    val attachFileFileSizes = attachFileFileSize.getJSONArray("string")
                                    for (i in 0 until attachFileFileSizes.length()) {
                                        attachItems.add(AttachItem(
                                                attachFileNames.get(i).toString().dropLast(1),
                                                attachFileUrls.get(i).toString().dropLast(1),
                                                attachFileFileSizes.get(i).toString().dropLast(1)
                                        ))
                                    }

                                } catch (e: JSONException) {
                                    if (attachFileName.getString("string") != "") {
                                        attachItems.add(AttachItem(
                                                attachFileName.getString("string").dropLast(1),
                                                attachFileFileSize.getString("string").dropLast(1),
                                                attachFileUrl.getString("string").dropLast(1)
                                        ))
                                    }
                                }
                                announcement_attach.layoutManager = LinearLayoutManager(this)
//                                announcement_attach.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                                announcement_attach.adapter= AnnAttachmentAdapter(attachItems){
                                    val intent = Intent()
                                    intent.setClass(this, AnnActivity::class.java)
                                    startActivity(intent)
                                }
                                loading.visibility = View.GONE
                                annContent.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }
}