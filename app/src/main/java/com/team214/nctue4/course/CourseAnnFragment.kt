package com.team214.nctue4.course


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team214.nctue4.AnnActivity
import com.team214.nctue4.R
import com.team214.nctue4.model.AnnItem
import com.team214.nctue4.utility.DataStatus
import com.team214.nctue4.utility.OldE3AnnFrom
import com.team214.nctue4.utility.OldE3Connect
import com.team214.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.status_empty.*
import kotlinx.android.synthetic.main.fragment_course_ann.*
import kotlinx.android.synthetic.main.status_error.*


class CourseAnnFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
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


    override fun onRefresh() {
        announcement_refreshLayout.isRefreshing = false
        announcement_course_recycler_view.adapter?.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_course_ann, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    private fun getData() {
        error_request?.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE

        oldE3Service = (activity as CourseActivity).oldE3Service
        announcement_refreshLayout.setOnRefreshListener(this)
        val courseId = arguments!!.getString("courseId")
        val courseName = arguments!!.getString("courseName")
        oldE3Service.getCourseAnn(courseId, courseName) { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    if (response != null) {
                        update(response)
                    }
                }
                else -> {
                    error_request.visibility = View.VISIBLE
                    dataStatus = DataStatus.INIT
                    error_request_retry.setOnClickListener { getData() }
                }
            }
            dataStatus = DataStatus.FINISHED
            progress_bar.visibility = View.GONE
        }
    }

    private fun update(annItems: ArrayList<AnnItem>) {
        if (annItems.size == 0) {
            empty_request.visibility = View.VISIBLE
        } else {
            announcement_course_recycler_view?.layoutManager = LinearLayoutManager(context)
            announcement_course_recycler_view?.addItemDecoration(DividerItemDecoration(context,
                    LinearLayoutManager.VERTICAL))
            announcement_course_recycler_view?.adapter = CourseAnnAdapter(annItems) {
                val intent = Intent()
                intent.setClass(activity, AnnActivity::class.java)
                intent.putExtra("annId", it.bulletinId)
                intent.putExtra("courseName", it.courseName)
                intent.putExtra("courseId", it.courseId)
                intent.putExtra("loginTicket", oldE3Service.getCredential().first)
                intent.putExtra("accountId", oldE3Service.getCredential().second)
                intent.putExtra("from", OldE3AnnFrom.COURSE)
                startActivity(intent)
            }
            announcement_refreshLayout.visibility = View.VISIBLE
        }
    }
}
