package com.emerssso.livetweet

import android.app.Application

import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig

import io.fabric.sdk.android.Fabric

/**
 * Created by Conner on 10/30/2016.
 */

class LiveTweetApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val authConfig = TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET)
        Fabric.with(this, Twitter(authConfig))
    }

    companion object {

        // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
        private val TWITTER_KEY = "Fitx6SIezqH4LYcoL2qYocgxB"
        private val TWITTER_SECRET = "3m3uz96DI2ZyiuO8RZxIZPuSK6HyKaXeYn1NiTXGwK2mrZOsmJ"
    }
}
