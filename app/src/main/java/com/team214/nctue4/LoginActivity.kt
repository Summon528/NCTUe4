package com.team214.nctue4

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.team214.nctue4.utility.OldE3Connect
import com.team214.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.activity_login.*
import android.support.v4.content.ContextCompat.startActivity


class LoginActivity : AppCompatActivity() {
    private lateinit var oldE3Service: OldE3Connect

    override fun onStop() {
        super.onStop()
        if (::oldE3Service.isInitialized) oldE3Service.cancelPendingRequests()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (intent.getBooleanExtra("logout", false)) {
            Toast.makeText(this@LoginActivity, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
            val prefsEditor = prefs.edit()
            prefsEditor.remove("studentId")
            prefsEditor.remove("studentPassword")
            prefsEditor.apply()
        } else {
            val studentId = prefs.getString("studentId", "")
            val studentPassword = prefs.getString("studentPassword", "")
            if (studentId != "" && studentPassword != "") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        setContentView(R.layout.activity_login)
        login_button.setOnClickListener {
            student_id.isEnabled = false
            student_password.isEnabled = false
            login_progressbar.visibility = View.VISIBLE
            val studentId = student_id.text.toString()
            val studentPassword = student_password.text.toString()
            val service = OldE3Connect(studentId, studentPassword)
            service.getLoginTicket { status, response ->
                when (status) {
                    OldE3Interface.Status.SUCCESS -> {
                        val prefsEditor = prefs.edit()
                        prefsEditor.putString("studentId", studentId)
                        prefsEditor.putString("studentPassword", studentPassword)
                        prefsEditor.putString("studentName", response!!.first)
                        prefsEditor.putString("studentEmail", response.second)
                        prefsEditor.apply()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    OldE3Interface.Status.WRONG_CREDENTIALS -> {
                        login_error_text_view.text = getString(R.string.login_id_or_password_error)
                        login_error_text_view.visibility = View.VISIBLE
                        login_progressbar.visibility = View.GONE
                        student_id.isEnabled = true
                        student_password.isEnabled = true
                    }
                    else -> {
                        login_error_text_view.text = getString(R.string.generic_error)
                        login_error_text_view.visibility = View.VISIBLE
                        login_progressbar.visibility = View.GONE
                        student_id.isEnabled = true
                        student_password.isEnabled = true
                    }
                }
            }

        }
    }
}
