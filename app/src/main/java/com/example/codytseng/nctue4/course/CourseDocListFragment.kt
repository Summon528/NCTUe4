package com.example.codytseng.nctue4.course

import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.codytseng.nctue4.R
import com.example.codytseng.nctue4.model.DocGroupItem
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


    private fun updateList(data: JSONArray) {
        val docGroupItems = ArrayList<DocGroupItem>()
        for (i in 0 until data.length()) {
            val tmp = data.get(i) as JSONObject
            docGroupItems.add(DocGroupItem(tmp.getString("DisplayName"),
                    tmp.getString("DocumentId"),
                    tmp.getString("CourseId")))
            course_doc_list_recycler_view.layoutManager = LinearLayoutManager(context)
            course_doc_list_recycler_view.adapter = CourseDocListAdapter(docGroupItems) {
                val dialog = CourseDocDialog()
                val bundle = Bundle()
                bundle.putString("documentId",it.documentId)
                bundle.putString("courseId",it.courseId)
                dialog.arguments = bundle
                dialog.show(fragmentManager,"TAG")
            }

        }

    }


}