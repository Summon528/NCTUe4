package com.team214.nctue4


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import com.team214.nctue4.utility.DataStatus
import com.team214.nctue4.utility.OldE3Connect
import com.team214.nctue4.utility.OldE3Interface
import com.team214.nctue4.utility.downloadFile
import kotlinx.android.synthetic.main.activity_ann.*
import kotlinx.android.synthetic.main.status_error.*


class AnnActivity : AppCompatActivity() {
    private lateinit var oldE3Service: OldE3Connect
    private var dataStatus = DataStatus.INIT

    override fun onStop() {
        super.onStop()
        oldE3Service.cancelPendingRequests()
        if (dataStatus == DataStatus.INIT) dataStatus = DataStatus.STOPPED
    }

    override fun onStart() {
        super.onStart()
        if (dataStatus == DataStatus.STOPPED) getData()
    }

    private lateinit var uri: String
    private lateinit var fileName: String


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    downloadFile(fileName, uri, this, this, ann_root, null)
                }
                return
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ann)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        getData()
    }

    private fun getData() {
        val bundle = intent.extras
        val loginTicket = bundle.getString("loginTicket")
        val accountId = bundle.getString("accountId")
        val annId = bundle.getString("annId")
        val from = bundle.getInt("from")
        val courseId = bundle.getString("courseId")
        error_request?.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE

        oldE3Service = OldE3Connect(loginTicket = loginTicket, accountId = accountId)
        oldE3Service.getAnnouncementDetail(annId, from, courseId) { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    error_request.visibility = View.GONE
                    // replace <img src="/...> to <img src="http://e3.nctu.edu.tw/..."
                    val content = response!!.content.replace("(?<=(<img[.\\s\\S^>]{0,300}src[ \n]{0,300}=[ \n]{0,300}\"))(/)(?=([^/]))".toRegex(),
                            "http://e3.nctu.edu.tw/")
                    ann_caption.text = response.caption
                    ann_courseName.text = response.courseName
                    ann_date.text = response.beginDate
                    ann_content_web_view.settings.defaultTextEncodingName = "utf-8"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) ann_content_web_view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                    ann_content_web_view.loadData(content, "text/html; charset=utf-8", "UTF-8")
                    ann_content_web_view.setBackgroundColor(Color.TRANSPARENT)
                    announcement_attach.layoutManager = LinearLayoutManager(this)
                    announcement_attach.adapter = AnnAttachmentAdapter(this, response.attachItems) {
                        uri = it.url
                        fileName = it.name
                        downloadFile(fileName, uri, this, this, ann_root) {
                            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    0)
                        }

                    }
                    ann_container.visibility = View.VISIBLE
                }
                else -> {
                    error_request.visibility = View.VISIBLE
                    dataStatus = DataStatus.INIT
                    error_request_retry.setOnClickListener { getData() }
                }
            }
            progress_bar.visibility = View.GONE
            dataStatus = DataStatus.FINISHED
        }
    }
}