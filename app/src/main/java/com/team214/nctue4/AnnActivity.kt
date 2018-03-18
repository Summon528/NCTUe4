package com.team214.nctue4


import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.team214.nctue4.utility.DataStatus
import com.team214.nctue4.utility.OldE3Connect
import com.team214.nctue4.utility.OldE3Interface
import com.team214.nctue4.utility.htmlCleaner
import kotlinx.android.synthetic.main.activity_ann.*
import kotlinx.android.synthetic.main.status_error.*
import java.io.File


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

    private fun downloadFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0)
        } else {
            val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DOWNLOADS +
                    "/" + getString(R.string.app_name) + "/" + fileName)
            val extension = MimeTypeMap.getFileExtensionFromUrl(fileName)
            val type = if (extension != null) {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            } else "application/octet-stream"
            if (file.exists()) {
                AlertDialog.Builder(this)
                        .setMessage(getString(R.string.detect_same_file))
                        .setPositiveButton(R.string.download_again, { dialog, which ->
                            file.delete()
                            downloadFile()
                        })
                        .setNegativeButton(R.string.open_existed, { dialog, which ->
                            val intent = Intent(Intent.ACTION_VIEW)
                            val fileUri = FileProvider.getUriForFile(this, this.applicationContext.packageName +
                                    ".com.team214", file)
                            intent.setDataAndType(fileUri, type)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            startActivity(intent)

                        })
                        .show()
            } else {
                val request = DownloadManager.Request(Uri.parse(uri))
                request.setTitle(fileName)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getString(R.string.app_name) + "/$fileName")
                request.setVisibleInDownloadsUi(true)
                request.setMimeType(type)
                Toast.makeText(this, R.string.download_start, Toast.LENGTH_SHORT).show()
                manager.enqueue(request)
            }
        }
    }

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
                    downloadFile()
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
                    ann_caption.text = response!!.caption
                    ann_courseName.text = response.courseName
                    ann_date.text = response.beginDate
                    ann_content.text =
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
                        uri = it.url
                        fileName = it.name
                        downloadFile()
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