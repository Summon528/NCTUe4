package com.example.codytseng.nctue4

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var currentFragment = -1
    lateinit var oldE3Service: OldE3Connect

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("currentFragment", currentFragment)
    }

    override fun onStop() {
        super.onStop()
        oldE3Service.cancelPendingRequests()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        updateNavDrawerData {
            switchFragment(
                    if (savedInstanceState?.getInt("currentFragment") != null)
                        savedInstanceState.getInt("currentFragment")
                    else -1
            )
        }

    }

    private fun updateNavDrawerData(completionHandler: () -> Unit) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val studentId = prefs.getString("studentId", "")
        val studentPassword = prefs.getString("studentPassword", "")
        oldE3Service = OldE3Connect(studentId, studentPassword)
        oldE3Service.getLoginTicket { status, response ->
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    nav_view.getHeaderView(0).findViewById<TextView>(R.id.student_name).text = response!!.first
                    nav_view.getHeaderView(0).findViewById<TextView>(R.id.student_email).text = response.second
                }
            }
            completionHandler()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                updateNavDrawerData {
                    switchFragment(currentFragment)
                }
            }
        }
    }

    private fun switchFragment(id: Int) {
        val fragment = when (id) {
            R.id.nav_home -> {
                currentFragment = id
                HomeFragment()
            }
            R.id.nav_starred_courses -> {
                currentFragment = id
                StarredCoursesE3Fragment()
            }
            R.id.nav_old_e3 -> {
                currentFragment = id
                OldE3Fragment()
            }
            R.id.nav_new_e3 -> {
                currentFragment = id
                NewE3Fragment()
            }
            R.id.nav_switch_account -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivityForResult(intent, 1)
                null
            }
            else -> {
                HomeFragment()
            }
        }
        if (fragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment, "main_fragment").commit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        switchFragment(item.itemId)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
