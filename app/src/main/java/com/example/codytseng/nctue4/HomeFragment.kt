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
import com.example.codytseng.nctue4.model.AnnouncementItem
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.home_fragment.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by CodyTseng on 3/12/2018.
 */

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener  {
    override fun onRefresh() {
        announcement_refreshLayout.isRefreshing = false
        announcement_login_recycler_view.adapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.home_fragment, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        announcement_refreshLayout.setOnRefreshListener(this)
        getData()
    }
    private fun getData(){
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
                                getAnnouncement(response!!)
                            }
                        }
                    }
                }
            }
        }

    }

    private fun getAnnouncement(courseList: JSONArray) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val studentId = prefs.getString("studentId", "")
        val studentPassword = prefs.getString("studentPassword", "")
        val service = OldE3Connect()
        service.getLoginTicket(studentId, studentPassword) { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    service.getAnnouncementList_Login { status, response ->
                        when (status) {
                            OldE3Interface.Status.SUCCESS -> {
                                updateList(response!!, courseList)
                            }
                        }
                    }
                }
            }
        }

    }
    private fun updateList(data: JSONArray, courseList: JSONArray) {
        val announcementItems = ArrayList<AnnouncementItem>()
        var courseDetail = HashMap<String, String>()
        for (i in 0 until courseList.length()) {
            val tmp = courseList.get(i) as JSONObject
            courseDetail.put(tmp.getString("CourseId"), tmp.getString("CourseName"))
        }
        for (i in 0 until data.length()) {
            val tmp = data.get(i) as JSONObject
            announcementItems.add(AnnouncementItem(
                    tmp.getInt("BulType"),
                    tmp.getString("BulletinId"),
                    courseDetail.get(tmp.getString("CourseId"))!!,
                    tmp.getString("Caption"),
                    tmp.getString("Content"),
                    tmp.getString("BeginDate"),
                    tmp.getString("EndDate")
                )
            )
        }
        Log.d("TEST",announcementItems.toString())
        announcement_login_recycler_view.layoutManager = LinearLayoutManager(context)
        announcement_login_recycler_view.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        announcement_login_recycler_view.adapter= AnnouncementAdapter(announcementItems){
            val intent = Intent()
            intent.setClass(activity, AnnActivity::class.java)
            intent.putExtra("annId", it.mBulletinId)
            intent.putExtra("courseName", it.mCourseName)
            startActivity(intent)
        }
    }
}