package com.example.codytseng.nctue4

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.example.codytseng.nctue4.utility.OldE3Connect
import com.example.codytseng.nctue4.utility.OldE3Interface
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    lateinit var oldE3Service: OldE3Connect

    override fun onStop() {
        super.onStop()
        if (::oldE3Service.isInitialized) oldE3Service.cancelPendingRequests()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        student_id.setText(prefs.getString("studentId", ""))
        student_password.setText(prefs.getString("studentPassword", ""))
        login_button.setOnClickListener {
            login_progressbar.visibility = View.VISIBLE;
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
                        login_progressbar.visibility = View.GONE;
                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent);
                        Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    OldE3Interface.Status.WRONG_CREDENTIALS -> {
                        login_error_textview.text = getString(R.string.login_id_or_password_error)
                        login_error_textview.visibility = View.VISIBLE
                        login_progressbar.visibility = View.GONE;
                    }
                    else -> {
                        login_error_textview.text = getString(R.string.generic_error)
                        login_error_textview.visibility = View.VISIBLE
                        login_progressbar.visibility = View.GONE;
                    }
                }
            }
        }

        logout_button.setOnClickListener {
            val prefsEditor = prefs.edit()
            prefsEditor.remove("studentId")
            prefsEditor.remove("studentPassword")
            prefsEditor.remove("studentName")
            prefsEditor.remove("studentEmail")
            prefsEditor.apply()
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent);
            Toast.makeText(this@LoginActivity, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
