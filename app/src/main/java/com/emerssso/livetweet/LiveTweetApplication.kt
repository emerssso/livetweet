package com.emerssso.livetweet

import android.app.Application
import com.twitter.sdk.android.core.Twitter

class LiveTweetApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Twitter.initialize(this)
    }
}
