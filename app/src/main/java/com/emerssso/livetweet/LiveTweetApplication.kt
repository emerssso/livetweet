package com.emerssso.livetweet

import android.app.Application
import android.util.Log
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig

class LiveTweetApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Twitter.initialize(TwitterConfig.Builder(this)
                .logger(DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(TwitterAuthConfig(BuildConfig.TWITTER_CONSUMER_KEY,
                        BuildConfig.TWITTER_CONSUMER_SECRET))
                .debug(BuildConfig.DEBUG)
                .build())
    }
}
