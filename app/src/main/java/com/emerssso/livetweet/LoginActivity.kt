package com.emerssso.livetweet

import android.content.Intent
import android.os.Bundle
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*

/** 
 * Activity responsible for initial user login. 
 * Also enforces acceptance of privacy policy.
 */
class LoginActivity : ParentActivity(), AnkoLogger {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (sessionManager.activeSession != null) {
            startActivity(intentFor<TweetActivity>())
            finish()
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        twitterLoginButton.onActivityResult(requestCode, resultCode, data)
    }
}
