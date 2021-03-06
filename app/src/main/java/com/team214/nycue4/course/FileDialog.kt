package com.team214.nycue4.course

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.team214.nycue4.R
import com.team214.nycue4.client.E3Client
import com.team214.nycue4.model.FileItem
import com.team214.nycue4.model.FolderItem
import com.team214.nycue4.utility.ThemedDialogFragment
import com.team214.nycue4.utility.downloadFile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.dialog_course_folder.*

class FileDialog : ThemedDialogFragment() {
    private lateinit var client: E3Client
    private lateinit var folderItem: FolderItem
    private lateinit var url: String
    private lateinit var fileName: String
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        client = (activity as CourseActivity).client
        folderItem = arguments!!.getParcelable("folderItem")!!
        return inflater.inflate(R.layout.dialog_course_folder, container, false)
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    private fun getData() {
        disposable = client.getFiles(folderItem)
            .observeOn(AndroidSchedulers.mainThread())
            .collectInto(mutableListOf<FileItem>()) { fileItems, fileItem -> fileItems.add(fileItem) }
            .doFinally { progress_bar?.visibility = View.GONE }
            .subscribeBy(
                onSuccess = { displayData(it) },
                onError = {
                    if (!(context as Activity).isFinishing) {
                        Toast.makeText(context, getString(R.string.generic_error), Toast.LENGTH_SHORT).show()
                    }
                    dismissAllowingStateLoss()
                }
            )
    }

    private fun displayData(fileItems: MutableList<FileItem>) {
        course_doc_dialog_recycler_view.layoutManager = LinearLayoutManager(context)
        course_doc_dialog_recycler_view.adapter = FileAdapter(context!!, fileItems) {
            url = it.url
            fileName = it.name
            downloadFile(fileName, url, context!!) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
                )
            }
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                dismissAllowingStateLoss()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            0 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    downloadFile(
                        fileName,
                        url,
                        context!!,
                        null,
                        null
                    )
                    dismissAllowingStateLoss()
                }
                return
            }
        }
    }
}