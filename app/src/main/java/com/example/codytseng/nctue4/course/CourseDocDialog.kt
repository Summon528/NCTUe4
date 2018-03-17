package com.example.codytseng.nctue4.course


import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.example.codytseng.nctue4.R
import com.example.codytseng.nctue4.model.DocItem
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.dialog_course_doc.*


class CourseDocDialog : DialogFragment() {

    private lateinit var oldE3Service: OldE3Connect

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.dialog_course_doc, container, false)
    }

    override fun onStop() {
        super.onStop()
        oldE3Service.cancelPendingRequests()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        oldE3Service = (activity as CourseActivity).oldE3Service
        oldE3Service.getAttachFileList(arguments.getString("documentId"),
                arguments.getString("courseId"), { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    updateList(response!!)
                }
            }
        })
    }

    private lateinit var uri: String
    private lateinit var fileName: String

    private fun updateList(docItems: ArrayList<DocItem>) {
        course_doc_dialog_recycler_view?.layoutManager = LinearLayoutManager(context)
        course_doc_dialog_recycler_view?.adapter = CourseDocAdapter(docItems) {
            uri = it.docPath
            fileName = it.displayName
            downloadFile()
        }
        progress_bar.visibility = View.GONE

    }

    private fun downloadFile() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0)
        } else {
            val request = DownloadManager.Request(Uri.parse(uri))
            request.setTitle(fileName)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/$fileName")
            request.setVisibleInDownloadsUi(true)
            Toast.makeText(context, R.string.download_start, Toast.LENGTH_SHORT).show()
            manager.enqueue(request)
            dismiss()
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
}

