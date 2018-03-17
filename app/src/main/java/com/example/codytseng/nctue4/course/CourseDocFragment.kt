package com.example.codytseng.nctue4.course


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.codytseng.nctue4.R
import kotlinx.android.synthetic.main.fragment_course_doc.*

class CourseDocFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_course_doc, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    private fun getData() {
        val bundle0 = Bundle()
        val bundle1 = Bundle()
        val courseId = arguments.getString("courseId")
        bundle0.putString("docType", "0")
        bundle1.putString("docType", "1")
        bundle0.putString("courseId", courseId)
        bundle1.putString("courseId", courseId)
        val fragments = listOf(CourseDocListFragment(), CourseDocListFragment())
        fragments[0].arguments = bundle0
        fragments[1].arguments = bundle1
        val adapter = CourseDocGroupAdapter(activity.supportFragmentManager,
                fragments,
                listOf(getString(R.string.course_doc_type_handout), getString(R.string.course_doc_type_reference)))
        course_doc_view_pager?.adapter = adapter
        course_doc_tab_layout?.setupWithViewPager(course_doc_view_pager)
    }
}
