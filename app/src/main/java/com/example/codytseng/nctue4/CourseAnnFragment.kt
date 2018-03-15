package com.example.codytseng.nctue4


import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.fragment_course_ann.*


/**
 * A simple [Fragment] subclass.
 */
class CourseAnnFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_course_ann, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val service = OldE3Connect()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val studentId = prefs.getString("studentId", "")
        val studentPassword = prefs.getString("studentPassword", "")
        Log.d("arguments", arguments.toString())
        val courseId = arguments.getString("courseId")
        val courseName = arguments.getString("courseName")
        service.getLoginTicket(studentId, studentPassword) { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    service.getCourseAnn(courseId) { status, response ->
                        when (status) {
                            OldE3Interface.Status.SUCCESS -> {
                                if (course_ann_text_view != null)
                                    course_ann_text_view.text = response.toString()
                            }
                        }
                    }
                }
            }
        }
    }

}// Required empty public constructor
