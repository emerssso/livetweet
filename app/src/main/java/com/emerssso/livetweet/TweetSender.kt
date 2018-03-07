package com.emerssso.livetweet

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Media
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.MediaService
import com.twitter.sdk.android.core.services.StatusesService
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.info
import org.jetbrains.anko.warn
import java.util.*

/**
 * Class responsible for sending tweets to Twitter APIs via [statusesService].
 * Tweets may optionally include images, which are sent via [mediaService].
 */
class TweetSender(private val statusesService: StatusesService,
                  private val mediaService: MediaService) : AnkoLogger {

    private val messages = ArrayDeque<Status>()
    var lastId: Long? = null
    private var inProgress = false
    private val callbacks = mutableListOf<FailureCallback>()

    interface FailureCallback {
        fun onFailure(exception: TwitterException?)
    }

    fun queueTweet(status: Status) {
        if (!inProgress) {
            sendTweet(status)
        } else {
            messages.add(status)
        }
    }

    fun registerCallback(callback: FailureCallback) {
        callbacks.add(callback)
    }

    fun unregisterCallback(callback: FailureCallback) {
        callbacks.remove(callback)
    }

    private fun sendTweet(status: Status) {
        inProgress = true
        if(status.photo != null) {

            val media = RequestBody.create(MediaType.parse("image/*"), status.photo)

            val call = mediaService.upload(media, null, null)
            call?.enqueue(object : Callback<Media>() {
                override fun success(result: Result<Media>?) {
                    val mediaId = result?.data?.mediaIdString
                    info("media uploaded successfully with ID: $mediaId " +
                            "and result code ${result?.response?.code()}")
                    sendMessage(status, mediaId)
                }

                override fun failure(exception: TwitterException?) {
                    warn("unable to upload photo: ${exception?.getStackTraceString()}")
                    notifyCallbacksOfFailure(exception)
                    sendMessage(status)
                }
            })
        } else {
            sendMessage(status)
        }
    }

    private fun sendMessage(status: Status, mediaId: String? = null) {
        val call = statusesService.update(status.message, lastId,
                false, null, null, null, null, true, mediaId)

        call?.enqueue(object : Callback<Tweet>() {
            override fun success(result: Result<Tweet>?) {
                lastId = result?.data?.id
                sendNextTweet()
            }

            override fun failure(exception: TwitterException?) {
                warn("failure: ${exception?.getStackTraceString()}")
                notifyCallbacksOfFailure(exception)
                sendNextTweet()
            }
        })
    }

    private fun sendNextTweet() {
        if (messages.isNotEmpty()) {
            sendTweet(messages.remove())
        } else {
            inProgress = false
        }
    }

    private fun notifyCallbacksOfFailure(exception: TwitterException?) {
        for(callback in callbacks) {
            callback.onFailure(exception)
        }
    }
}
