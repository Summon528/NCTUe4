package com.team214.nctue4.course


import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.team214.nctue4.R
import com.team214.nctue4.model.AttachItem
import com.team214.nctue4.utility.DataStatus
import com.team214.nctue4.utility.OldE3Connect
import com.team214.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.dialog_course_doc.*
import java.io.File


class CourseDocDialog : DialogFragment() {

    private lateinit var oldE3Service: OldE3Connect
    private var dataStatus = DataStatus.INIT

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.dialog_course_doc, container, false)
    }

    override fun onStop() {
        super.onStop()
        oldE3Service.cancelPendingRequests()
        if (dataStatus == DataStatus.INIT) dataStatus = DataStatus.STOPPED
    }

    override fun onStart() {
        super.onStart()
        if (dataStatus == DataStatus.STOPPED) getData()
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        getData()
    }

    private lateinit var uri: String
    private lateinit var fileName: String

    private fun getData() {
        oldE3Service = (activity as CourseActivity).oldE3Service
        oldE3Service.getAttachFileList(arguments.getString("documentId"),
                arguments.getString("courseId"), { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    updateList(response!!)
                }
                else -> {
                    Toast.makeText(context, getString(R.string.generic_error), Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        })
    }

    private fun updateList(docItems: ArrayList<AttachItem>) {
        course_doc_dialog_recycler_view?.layoutManager = LinearLayoutManager(context)
        course_doc_dialog_recycler_view?.adapter = CourseDocAdapter(docItems) {
            uri = it.url
            fileName = it.name
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
            val file = File(Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DOWNLOADS +
                    "/" + getString(R.string.app_name) + "/" + fileName)
            if (file.exists()) {
                AlertDialog.Builder(context)
                        .setMessage(getString(R.string.detect_same_file))
                        .setPositiveButton(R.string.download_again, DialogInterface.OnClickListener { dialog, which ->
                            file.delete()
                            downloadFile()
                        })
                        .setNegativeButton(R.string.open_existed, DialogInterface.OnClickListener { dialog, which ->
                            val intent = Intent(Intent.ACTION_VIEW)
                            val extension = MimeTypeMap.getFileExtensionFromUrl(fileName)
                            val type = if (extension != null) {
                                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                            } else null
                            val fileUri = FileProvider.getUriForFile(context, context.applicationContext.packageName +
                                    ".com.team214", file);
                            intent.setDataAndType(fileUri, type);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                            dismiss()
                        })
                        .show()
            } else {
                val request = DownloadManager.Request(Uri.parse(uri))
                request.setTitle(fileName)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getString(R.string.app_name) + "/$fileName")
                request.setVisibleInDownloadsUi(true)
                Toast.makeText(context, R.string.download_start, Toast.LENGTH_SHORT).show()
                manager.enqueue(request)
                dismiss()
            }
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

