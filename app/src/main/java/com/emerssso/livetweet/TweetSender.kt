package com.emerssso.livetweet

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.StatusesService
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.warn
import java.util.*

class TweetSender(private val statusesService: StatusesService) : AnkoLogger {

    private val messages = ArrayDeque<String>()
    var lastId: Long? = null
    private var inProgress = false

    fun queueTweet(message: String) {
        if (messages.isEmpty() && !inProgress) {
            sendTweet(message)
        } else {
            messages.add(message)
        }
    }

    private fun sendTweet(message: String) {
        inProgress = true
        val call = statusesService.update(message, lastId, false, null, null, null, null, true, null)
        call?.enqueue(object : Callback<Tweet>() {
            override fun success(result: Result<Tweet>) {
                lastId = result.data.id
                sendNextTweet()
            }

            override fun failure(exception: TwitterException) {
                warn("failure: ${exception.getStackTraceString()}")
                sendNextTweet()
            }
        })
    }

    private fun sendNextTweet() {
        if (!messages.isEmpty()) {
            sendTweet(messages.remove())
        } else {
            inProgress = false
        }
    }
}
