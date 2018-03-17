package com.example.codytseng.nctue4.course

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.R
import com.example.codytseng.nctue4.model.DocGroupItem
import com.example.codytseng.nctue4.utility.DataStatus
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.fragment_course_doc_list.*


class CourseDocListFragment : Fragment() {

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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_course_doc_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    private fun getData() {
        oldE3Service = (activity as CourseActivity).oldE3Service
        val docType = arguments.getString("docType")
        val courseId = arguments.getString("courseId")
        oldE3Service.getMaterialDocList(courseId, docType) { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    updateList(response!!)
                }
            }
            dataStatus = DataStatus.FINISHED
        }
    }

    private fun updateList(docGroupItems: ArrayList<DocGroupItem>) {
        course_doc_list_recycler_view?.layoutManager = LinearLayoutManager(context)
        course_doc_list_recycler_view?.adapter = CourseDocListAdapter(docGroupItems) {
            val dialog = CourseDocDialog()
            val bundle = Bundle()
            bundle.putString("documentId", it.documentId)
            bundle.putString("courseId", it.courseId)
            dialog.arguments = bundle
            dialog.show(fragmentManager, "TAG")
        }
        course_doc_list_recycler_view.visibility = View.VISIBLE
        progress_bar.visibility = View.GONE
    }
}