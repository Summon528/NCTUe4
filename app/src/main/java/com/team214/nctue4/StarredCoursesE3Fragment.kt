package com.team214.nctue4

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team214.nctue4.course.CourseActivity
import com.team214.nctue4.model.CourseItem
import com.team214.nctue4.utility.DataStatus
import com.team214.nctue4.utility.OldE3Connect
import com.team214.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.fragment_old_e3.*
import kotlinx.android.synthetic.main.item_course.view.*
import kotlinx.android.synthetic.main.status_empty.*
import kotlinx.android.synthetic.main.status_error.*


class StarredCoursesE3Fragment : Fragment() {
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (arguments?.getBoolean("home") == null)
            activity!!.setTitle(R.string.starred_courses)
        return inflater.inflate(R.layout.fragment_old_e3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    private fun getData() {
        error_request?.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE

        oldE3Service = (activity as MainActivity).oldE3Service
        oldE3Service.getCourseList { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    updateList(response!!)
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

    private fun updateList(courseItems: ArrayList<CourseItem>) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val oldE3Starred = prefs.getStringSet("oldE3Starred", HashSet<String>())
        if (oldE3Starred.isEmpty()) empty_request.visibility = View.VISIBLE
        val starredCourse =
                if (arguments?.getBoolean("home") != null) {
                    val tmp = courseItems.filter { oldE3Starred.contains(it.courseId) }
                    ArrayList(tmp.subList(0, minOf(5, tmp.size)))
                } else ArrayList(courseItems.filter { oldE3Starred.contains(it.courseId) })
        if (starredCourse.isEmpty()) empty_request.visibility = View.VISIBLE
        else {
            old_e3_recycler_view?.layoutManager = LinearLayoutManager(context)
            if (arguments?.getBoolean("home") != null)
                old_e3_recycler_view?.isNestedScrollingEnabled = false
            old_e3_recycler_view?.addItemDecoration(DividerItemDecoration(context,
                    LinearLayoutManager.VERTICAL))
            old_e3_recycler_view?.adapter = CourseAdapter(starredCourse, HashSet(oldE3Starred),
                    context, fun(view: View, courseId: String) {
                if (oldE3Starred.contains(courseId)) {
                    oldE3Starred.remove(courseId)
                    view.course_star.setColorFilter(ContextCompat.getColor(context!!, R.color.md_grey_500))
                } else {
                    oldE3Starred.add(courseId)
                    view.course_star.setColorFilter(ContextCompat.getColor(context!!, R.color.md_orange_500))
                }
                prefs.edit().putStringSet("oldE3Starred", oldE3Starred).commit()
            }, {
                val intent = Intent()
                intent.setClass(activity, CourseActivity::class.java)
                intent.putExtra("courseId", it.courseId)
                intent.putExtra("courseName", it.courseName)
                startActivity(intent)
            })
            old_e3_recycler_view.visibility = View.VISIBLE
        }
        progress_bar.visibility = View.GONE
    }
}