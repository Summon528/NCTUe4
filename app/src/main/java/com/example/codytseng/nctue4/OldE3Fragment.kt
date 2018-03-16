package com.example.codytseng.nctue4

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.course.CourseActivity
import com.example.codytseng.nctue4.model.CourseItem
import com.example.codytseng.nctue4.utility.DataStatus
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.fragment_old_e3.*


class OldE3Fragment : Fragment() {
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
        return inflater?.inflate(R.layout.fragment_old_e3, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    private fun getData() {
        oldE3Service = (activity as MainActivity).oldE3Service
        oldE3Service.getCourseList { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    updateList(response!!)
                }
            }
            dataStatus = DataStatus.FINISHED
        }
    }

    private fun updateList(courseItems: ArrayList<CourseItem>) {
        old_e3_recycler_view?.layoutManager = LinearLayoutManager(context)
        old_e3_recycler_view?.adapter = CourseAdapter(courseItems) {
            val intent = Intent()
            intent.setClass(activity, CourseActivity::class.java)
            intent.putExtra("courseId", it.courseId)
            intent.putExtra("courseName", it.courseName)
            startActivity(intent)
        }
        progress_bar.visibility = View.GONE
        old_e3_recycler_view.visibility  = View.VISIBLE
    }
}