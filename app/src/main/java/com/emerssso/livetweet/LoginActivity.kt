package com.emerssso.livetweet

import android.content.Intent
import android.os.Bundle
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import org.jetbrains.anko.*

/** 
 * Activity responsible for initial user login. 
 * Also enforces acceptance of privacy policy.
 */
class LoginActivity : LauncherActivity(), AnkoLogger {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

    }
}
