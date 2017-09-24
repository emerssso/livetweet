package com.emerssso.livetweet

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*


class LoginActivity : AppCompatActivity(), AnkoLogger {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        versionNumber.text = getString(R.string.version_number, BuildConfig.VERSION_NAME)

        twitterLoginButton.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                startActivity(intentFor<TweetActivity>())
                finish()
            }

            override fun failure(exception: TwitterException) {
                info { "Login with Twitter failure: ${exception.getStackTraceString()}" }
                toast(R.string.login_failed)
            }
        }
    }

    @SuppressLint("InflateParams") //age old "can't pass the parent if it doesn't exist" problem
    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences(FIRST_TIME_USE, Context.MODE_PRIVATE)

        if (prefs.getBoolean(FIRST_TIME_USE, true)) {

            val message = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_privacy_policy_body, null)

            AlertDialog.Builder(this)
                    .setTitle(R.string.privacy_policy_title)
                    .setView(message)
                    .setPositiveButton(R.string.accept, { _, i ->
                        if (i == DialogInterface.BUTTON_POSITIVE) {
                            prefs.edit().putBoolean(FIRST_TIME_USE, false).apply()
                        }
                    })
                    .setNegativeButton(R.string.reject, { _, i ->
                        if (i == DialogInterface.BUTTON_NEGATIVE) {
                            finish()
                        }
                    })
                    .setOnCancelListener {
                        toast(R.string.policy_not_accepted)
                        finish()
                    }
                    .create().show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        twitterLoginButton.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_login_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_see_libraries -> {
                LibsBuilder().withActivityStyle(Libs.ActivityStyle.DARK)
                        .withAutoDetect(true)
                        .start(this)
                return true
            }
            else -> return super.onContextItemSelected(item)
        }
    }
}
