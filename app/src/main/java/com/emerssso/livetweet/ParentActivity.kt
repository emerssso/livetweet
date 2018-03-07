package com.emerssso.livetweet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

/** Parent Activity class which contains shared menu and privacy policy functionality */
abstract class ParentActivity : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_login_activity, menu)
        return true
    }

    @SuppressLint("InflateParams") //age old "can't pass the parent if it doesn't exist" problem
    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences(FIRST_TIME_USE, Context.MODE_PRIVATE)

        if (prefs.getBoolean(FIRST_TIME_USE, true)) {
            alert(R.string.privacy_policy_title) {
                customView = LayoutInflater.from(this@ParentActivity)
                        .inflate(R.layout.dialog_privacy_policy_body, null)

                positiveButton(R.string.accept) {
                    prefs.edit().putBoolean(FIRST_TIME_USE, false).apply()
                }

                negativeButton(R.string.reject) { finish() }

                onCancelled {
                    toast(R.string.policy_not_accepted)
                    finish()
                }
            }.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_see_libraries -> {
                LibsBuilder().withActivityStyle(Libs.ActivityStyle.DARK)
                        .withAutoDetect(true)
                        .start(this)
                return true
            }
            R.id.action_see_source -> {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/emerssso/livetweet")))
                return true
            }
            R.id.action_send_feedback -> {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/emerssso/livetweet/issues")))
                return true
            }
            else -> return super.onContextItemSelected(item)
        }
    }
}
