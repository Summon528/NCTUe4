package com.example.codytseng.nctue4.course

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.example.codytseng.nctue4.BlankFragment
import com.example.codytseng.nctue4.R
import com.example.codytseng.nctue4.utility.BottomNavigationViewHelper
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.activity_course.*

class CourseActivity : AppCompatActivity() {
    lateinit var oldE3Service: OldE3Connect
    private var currentFragment = -1

    override fun onStop() {
        super.onStop()
        oldE3Service.cancelPendingRequests()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("currentFragment", currentFragment)
    }

    private val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                switchFragment(item.itemId)
                return@OnNavigationItemSelectedListener true
            }


    private fun switchFragment(itemId: Int) {
        currentFragment = itemId
        val fragment = when (itemId) {
            R.id.course_nav_ann -> {
                CourseAnnFragment()
            }
            R.id.course_nav_doc -> {
                CourseDocFragment()
            }
            R.id.course_nav_assignment -> {
                BlankFragment()
            }
            R.id.course_nav_score -> {
                BlankFragment()
            }
            R.id.course_nav_other -> {
                BlankFragment()
            }
            else -> {
                CourseAnnFragment()
            }
        }
        fragment.arguments = intent.extras
        supportFragmentManager.beginTransaction().replace(R.id.course_container, fragment).commit()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val studentId = prefs.getString("studentId", "")
        val studentPassword = prefs.getString("studentPassword", "")
        BottomNavigationViewHelper.disableShiftMode(course_bottom_nav)
        oldE3Service = OldE3Connect(studentId, studentPassword)
        oldE3Service.getLoginTicket { status, _ ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    switchFragment(
                            if (savedInstanceState?.getInt("currentFragment") != null)
                                savedInstanceState.getInt("currentFragment")
                            else -1)
                }
            }
        }
        course_bottom_nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

}
