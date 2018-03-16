package com.example.codytseng.nctue4


import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.course.CourseAnnAdapter
import com.example.codytseng.nctue4.model.AnnouncementItem
import com.example.codytseng.nctue4.utility.HtmlCleaner
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.fragment_course_ann.*
import org.json.JSONArray
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
class CourseAnnFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        announcement_refreshLayout.isRefreshing = false
        if (announcement_course_recycler_view.adapter.notifyDataSetChanged() != null)
        announcement_course_recycler_view.adapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_course_ann, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        announcement_refreshLayout.setOnRefreshListener(this)
        getData()
    }

    private fun getData() {
        val service = OldE3Connect()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val studentId = prefs.getString("studentId", "")
        val studentPassword = prefs.getString("studentPassword", "")
        Log.d("arguments", arguments.toString())
        val courseId = arguments.getString("courseId")
        service.getLoginTicket(studentId, studentPassword) { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    service.getCourseAnn(courseId) { status, response ->
                        when (status) {
                            OldE3Interface.Status.SUCCESS -> {
                                if (response != null) {
                                    update(response!!)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun update(data: JSONArray) {
        val announcementItems = ArrayList<AnnouncementItem>()
        val courseName = arguments.getString("courseName")
        for (i in 0 until data.length()) {
            val tmp = data.get(i) as JSONObject
            announcementItems.add(AnnouncementItem(
                    tmp.getInt("BulType"),
                    tmp.getString("BulletinId"),
                    tmp.getString("Caption"),
                    courseName,
                    HtmlCleaner(tmp.getString("Content")),
                    tmp.getString("BeginDate"),
                    tmp.getString("EndDate")
            )
            )
        }
        Log.d("TEST",announcementItems.toString())
        announcement_course_recycler_view.layoutManager = LinearLayoutManager(context)
        announcement_course_recycler_view.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        announcement_course_recycler_view.adapter= CourseAnnAdapter(announcementItems) {
            val intent = Intent()
            intent.setClass(activity, AnnActivity::class.java)
            intent.putExtra("annId", it.bulletinId)
            intent.putExtra("courseName", it.courseName)
            startActivity(intent)
        }
    }

}// Required empty public constructor
