package com.example.codytseng.nctue4

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.course.CourseActivity
import com.example.codytseng.nctue4.model.CourseItem
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.old_e3_fragment.*
import org.json.JSONArray
import org.json.JSONObject


class OldE3Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.old_e3_fragment, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val studentId = prefs.getString("studentId", "")
        val studentPassword = prefs.getString("studentPassword", "")
        val service = OldE3Connect()
        service.getLoginTicket(studentId, studentPassword) { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    service.getCourseList { status, response ->
                        when (status) {
                            OldE3Interface.Status.SUCCESS -> {
                                updateList(response!!)
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateList(data: JSONArray) {
        val courseItems = ArrayList<CourseItem>()
        for (i in 0 until data.length()) {
            val tmp = data.get(i) as JSONObject
            courseItems.add(CourseItem(tmp.getInt("CourseNo"),
                    tmp.getString("CourseName"),
                    tmp.getString("TeacherName"),
                    tmp.getString("CourseId")))
        }
        Log.d("TEST", courseItems.toString())
        if (old_e3_recycler_view != null) {
            old_e3_recycler_view.layoutManager = LinearLayoutManager(context)
            old_e3_recycler_view.adapter = CourseAdapter(courseItems) {
                val intent = Intent()
                intent.setClass(activity, CourseActivity::class.java)
                intent.putExtra("courseId", it.mCourseId)
                intent.putExtra("courseName", it.mCourseName)
                startActivity(intent)
            }
        }
    }
}