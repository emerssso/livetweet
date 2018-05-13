package com.emerssso.livetweet

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import org.jetbrains.anko.*

class LoginFragment : Fragment(), AnkoLogger {

    lateinit var twitterLoginButton: TwitterLoginButton

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (sessionManager.activeSession != null) {
            startActivity(activity?.intentFor<TweetActivity>())
            //finish()
            //TODO: convert to navigation
        }

        val versionNumber = view.findViewById<TextView>(R.id.versionNumber)
        twitterLoginButton = view.findViewById(R.id.twitterLoginButton)

        versionNumber.text = getString(R.string.version_number, BuildConfig.VERSION_NAME)

        twitterLoginButton.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) = activity?.run {
                startActivity(intentFor<TweetActivity>())
                //TODO: replace with navigation
            } ?: warn { "Unable to start activity" }

            override fun failure(exception: TwitterException) {
                info { "Login with Twitter failure: ${exception.getStackTraceString()}" }
                activity?.toast(R.string.login_failed)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        twitterLoginButton.onActivityResult(requestCode, resultCode, data)
    }
}