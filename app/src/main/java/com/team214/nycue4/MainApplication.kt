package com.team214.nycue4

import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.team214.nycue4.client.E3Client
import com.team214.nycue4.client.NewE3ApiClient
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import java.io.IOException
import java.net.SocketException


class MainApplication : MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        when (prefs.getString("night_mode", "MODE_NIGHT_FOLLOW_SYSTEM")) {
            "MODE_NIGHT_FOLLOW_SYSTEM" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "MODE_NIGHT_YES" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "MODE_NIGHT_NO" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        RxJavaPlugins.setErrorHandler { e ->
            var c: Throwable? = null
            if (e is UndeliverableException) {
                c = e.cause
            }
            if (c is IOException ||
                c is SocketException ||
                c is InterruptedException ||
                c is E3Client.WrongCredentialsException ||
                c is E3Client.ServiceErrorException ||
                c is NewE3ApiClient.SitePolicyNotAgreedException ||
                c is NewE3ApiClient.TokenInvalidException
            ) {
                return@setErrorHandler
            }
            Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
        }
    }
}