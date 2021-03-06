package com.team214.nycue4.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.team214.nycue4.BaseActivity
import com.team214.nycue4.BuildConfig
import com.team214.nycue4.R
import com.team214.nycue4.SettingsFragment
import com.team214.nycue4.client.*
import com.team214.nycue4.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var newE3ApiClient: NewE3ApiClient
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val remoteConfigInstance = FirebaseRemoteConfig.getInstance()

    override fun onResume() {
        super.onResume()
        remoteConfigInstance.fetchAndActivate()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val studentEmail = prefs.getString("studentEmail", "")
        val studentName = prefs.getString("studentName", "")

        newE3ApiClient = E3Clients.getNewE3ApiClient(this)

        remoteConfigInstance.setDefaults(mapOf("use_api_for_home_ann" to true))
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfigInstance.setConfigSettingsAsync(configSettings)

        if (savedInstanceState == null) {
            switchFragment(
                when (intent?.getStringExtra("shortcut")) {
                    "nav_ann" -> R.id.nav_ann
                    "nav_bookmarked" -> R.id.nav_bookmarked
                    "nav_download" -> R.id.nav_download
                    "nav_new_e3" -> R.id.nav_new_e3
                    else -> R.id.nav_home
                }
            )
        }

        nav_view.getHeaderView(0).findViewById<TextView>(R.id.student_name).text = studentName
        nav_view.getHeaderView(0).findViewById<TextView>(R.id.student_email).text = studentEmail
    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
            supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName) == null -> switchFragment(R.id.nav_home)
            else -> super.onBackPressed()
        }
    }

    fun switchFragment(id: Int) {
        nav_view.setCheckedItem(id)
        val fragment = when (id) {
            R.id.nav_home -> {
                firebaseAnalytics
                    .setCurrentScreen(this, "HomeFragment", HomeFragment::class.java.simpleName)
                HomeFragment()
            }
            R.id.nav_ann -> {
                firebaseAnalytics
                    .setCurrentScreen(this, "HomeAnnFragment", HomeAnnFragment::class.java.simpleName)
                HomeAnnFragment()
            }
            R.id.nav_bookmarked -> {
                firebaseAnalytics
                    .setCurrentScreen(this, "BookmarkedFragment", BookmarkedFragment::class.java.simpleName)
                BookmarkedFragment()
            }
            R.id.nav_download -> {
                firebaseAnalytics
                    .setCurrentScreen(this, "DownloadFragment", DownloadFragment::class.java.simpleName)
                DownloadFragment()
            }
            R.id.nav_new_e3 -> {
                firebaseAnalytics
                    .setCurrentScreen(this, "NewE3Fragment", CourseListFragment::class.java.simpleName)
                CourseListFragment().apply {
                    val bundle = Bundle()
                    bundle.putSerializable("e3Type", E3Type.NEW)
                    this.arguments = bundle
                }
            }
            R.id.nav_timetable-> {
                firebaseAnalytics
                    .setCurrentScreen(this, "TimetableFragment", TimetableFragment::class.java.simpleName)
                TimetableFragment()
            }
            R.id.nav_edit-> {
                firebaseAnalytics
                    .setCurrentScreen(this, "TimetableEditFragment", TimetableEditFragment::class.java.simpleName)
                TimetableEditFragment()
            }
            R.id.settings -> {
                firebaseAnalytics
                    .setCurrentScreen(this, "SettingsFragment", CourseListFragment::class.java.simpleName)
                SettingsFragment()
            }
            R.id.nav_account_management -> {
                val intent = Intent()
                intent.setClass(this, LoginActivity::class.java)
                startActivity(intent)
                this.finish()
                null
            }
            R.id.nav_feedback -> {
                val emailUri = "mailto: team214.csv06@gmail.com?subject=NCTUE4Feedback&body=" +
                        "\n\n\nAPI Level: ${android.os.Build.VERSION.SDK_INT}\n" +
                        "Device: ${android.os.Build.DEVICE}\n" +
                        "Model: ${android.os.Build.MODEL}\n" +
                        "Build: ${android.os.Build.DISPLAY}\n" +
                        "App Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse(emailUri)
                startActivity(intent)
                null
            }
            R.id.nav_about -> {
                firebaseAnalytics.setCurrentScreen(this, "LicenseDialog", LicenseDialog::class.java.simpleName)
                LicenseDialog().show(supportFragmentManager, "TAG")
                null
            }
            else -> {
                firebaseAnalytics.setCurrentScreen(this, "HomeFragment", HomeAnnFragment::class.java.simpleName)
                HomeFragment()
            }
        }
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment, fragment.javaClass.simpleName).commit()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        switchFragment(item.itemId)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
