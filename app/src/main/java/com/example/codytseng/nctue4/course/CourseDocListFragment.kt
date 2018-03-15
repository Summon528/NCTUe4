package com.example.codytseng.nctue4.course

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.codytseng.nctue4.R
import com.example.codytseng.nctue4.model.DocItem
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.fragment_course_doc_list.*
import org.json.JSONArray
import org.json.JSONObject


/**
 * Created by CodyTseng on 3/14/2018.
 */
class CourseDocListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_course_doc_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val service = (activity as CourseActivity).service
        val docType = arguments.getString("docType")
        val courseId = arguments.getString("courseId")
        service.getMaterialDocList(courseId, docType) { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    updateList(response!!)
                }
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private var uri =  ArrayList<String>()
    private var fileName = ArrayList<String>()

    private fun updateList(data: JSONArray) {
        val docItems = ArrayList<DocItem>()
        for (i in 0 until data.length()) {
            val tmp = data.get(i) as JSONObject
            docItems.add(DocItem(tmp.getString("DisplayName"),
                    tmp.getString("DocumentId"),
                    tmp.getString("CourseId")))
            course_doc_list_recycler_view.layoutManager = LinearLayoutManager(context)
            course_doc_list_recycler_view.adapter = CourseDocListAdapter(docItems) {
                val service = (activity as CourseActivity).service
                service.getAttachFileList(it.documentId, it.courseId) { status, response ->
                    uri.add((response!![0] as JSONObject).getString("RealityFileName"))
                    fileName.add((response[0] as JSONObject).getString("DisplayFileName"))
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                0)
                    } else {
                        val request = DownloadManager.Request(Uri.parse(uri[0]))
                        request.setTitle(fileName[0])
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        Log.d("URI", uri[0])
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/" + fileName)
                        request.setVisibleInDownloadsUi(true)
                        Toast.makeText(activity, R.string.download_start, Toast.LENGTH_SHORT).show();
                        manager.enqueue(request)

                    }

                }
            }

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    val request = DownloadManager.Request(Uri.parse(uri[0]))
                    request.setTitle(fileName[0])
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    Log.d("URI", uri[0])
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/" + fileName)
                    request.setVisibleInDownloadsUi(true)
                    Toast.makeText(activity, R.string.download_start, Toast.LENGTH_SHORT).show();
                    manager.enqueue(request)


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.

            else -> {
                // Ignore all other requests.
            }
        }
    }
}