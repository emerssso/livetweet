package com.emerssso.livetweet

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder

/** Parent Activity class which contains shared menu functionality */
internal abstract class ParentActivity : AppCompatActivity() {
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
