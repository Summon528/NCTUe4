package com.team214.nctue4.course


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.team214.nctue4.R
import com.team214.nctue4.model.AttachItem
import com.team214.nctue4.utility.DataStatus
import com.team214.nctue4.utility.OldE3Connect
import com.team214.nctue4.utility.OldE3Interface
import com.team214.nctue4.utility.downloadFile
import kotlinx.android.synthetic.main.dialog_course_doc.*


class CourseDocDialog : DialogFragment() {

    private lateinit var oldE3Service: OldE3Connect
    private var dataStatus = DataStatus.INIT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_course_doc, container, false)
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        getData()
    }

    private lateinit var uri: String
    private lateinit var fileName: String

    private fun getData() {
        oldE3Service = (activity as CourseActivity).oldE3Service
        oldE3Service.getAttachFileList(arguments!!.getString("documentId"),
                arguments!!.getString("courseId"), { status, response ->
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
        course_doc_dialog_recycler_view?.adapter = CourseDocDialogAdapter(docItems) {
            uri = it.url
            fileName = it.name
            downloadFile(fileName, uri, context!!, activity!!, activity!!.findViewById(R.id.snack_bar)) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        0)
            }
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                dismiss()
            }

        }
        progress_bar.visibility = View.GONE

    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    downloadFile(fileName, uri, context!!, activity!!, activity!!.findViewById(R.id.snack_bar), null)
                    dismiss()
                }
                return
            }
        }
    }
}

