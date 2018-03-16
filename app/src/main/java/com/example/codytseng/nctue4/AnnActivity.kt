package com.example.codytseng.nctue4


import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import com.example.codytseng.nctue4.utility.htmlCleaner
import kotlinx.android.synthetic.main.activity_ann.*


class AnnActivity : AppCompatActivity() {
    private lateinit var oldE3Service: OldE3Connect

    override fun onStop() {
        super.onStop()
        oldE3Service.cancelPendingRequests()
    }

    private lateinit var uri: String
    private lateinit var fileName: String

    private fun downloadFile() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0)
        } else {
            val request = DownloadManager.Request(Uri.parse(uri))
            request.setTitle(fileName)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/$fileName")
            request.setVisibleInDownloadsUi(true)
            Toast.makeText(this, R.string.download_start, Toast.LENGTH_SHORT).show();
            manager.enqueue(request)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                if ((grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    downloadFile()
                }
                return
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ann)
        val bundle = intent.extras
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val studentId = prefs.getString("studentId", "")
        val studentPassword = prefs.getString("studentPassword", "")
        val annId = bundle.getString("annId")

        oldE3Service = OldE3Connect(studentId, studentPassword)
        oldE3Service.getLoginTicket { status, _ ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    oldE3Service.getAnnouncementDetail(annId) { status2, response ->
                        when (status2) {
                            OldE3Interface.Status.SUCCESS -> {
                                announctment_caption.text = response!!.caption
                                announctment_courseName.text = response.courseName
                                announctment_date.text = response.beginDate
                                announctment_content.text =
                                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                                            Html.fromHtml(htmlCleaner(response.content))
                                                    .replace("\\n\\n+".toRegex(), "\n\n")
                                        } else {
                                            Html.fromHtml(htmlCleaner(response.content),
                                                    Html.FROM_HTML_MODE_COMPACT)
                                                    .replace("\\n\\n+".toRegex(), "\n\n")
                                        }
                                announcement_attach.layoutManager = LinearLayoutManager(this)
                                announcement_attach.adapter = AnnAttachmentAdapter(response.attachItems) {
                                    uri = it.url.dropLast(1)
                                    fileName = it.name.dropLast(1)
                                    downloadFile()
                                }
                                loading_spinner.visibility = View.GONE
                                ann_content.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }
}