package com.team214.nctue4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.team214.nctue4.utility.DataStatus
import com.team214.nctue4.utility.OldE3Connect
import com.team214.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.widget.Toast


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var currentFragment = -1
    private var backPressOnce = false
    lateinit var oldE3Service: OldE3Connect
    private lateinit var studentId: String
    private lateinit var studentPassword: String
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("currentFragment", currentFragment)
    }

    private var dataStatus = DataStatus.INIT

    override fun onStop() {
        super.onStop()
        if (::oldE3Service.isInitialized) oldE3Service.cancelPendingRequests()
        if (dataStatus == DataStatus.INIT) dataStatus = DataStatus.STOPPED
    }


    override fun onStart() {
        super.onStart()
        if (dataStatus == DataStatus.STOPPED) getData { switchFragment(currentFragment) }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Main)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        studentId = prefs.getString("studentId", "")
        studentPassword = prefs.getString("studentPassword", "")
        oldE3Service = OldE3Connect(studentId, studentPassword)
        currentFragment = if (savedInstanceState?.getInt("currentFragment") != null)
            savedInstanceState.getInt("currentFragment")
        else -1
        getData {
            switchFragment(currentFragment)
        }


    }

    private fun getData(completionHandler: () -> Unit) {
        oldE3Service.getLoginTicket { status, response ->
            completionHandler()
            when (status) {
                OldE3Interface.Status.SUCCESS -> {
                    nav_view.getHeaderView(0).findViewById<TextView>(R.id.student_name).text = response!!.first
                    nav_view.getHeaderView(0).findViewById<TextView>(R.id.student_email).text = response.second
                }
            }
            main_container.visibility = View.VISIBLE
            dataStatus = DataStatus.FINISHED

        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (backPressOnce) {
                super.onBackPressed()
            } else {
                backPressOnce = true
                Toast.makeText(this, getString(R.string.double_back_to_exit), Toast.LENGTH_SHORT).show()
                Handler().postDelayed(Runnable { backPressOnce = false }, 2000)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
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
            R.id.nav_log_out -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("logout", true)
                startActivity(intent)
                finish()
                null
            }
            R.id.nav_about -> {
                LicenseDialog().show(supportFragmentManager, "TAG")
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
