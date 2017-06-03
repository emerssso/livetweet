package com.emerssso.livetweet

import java.io.File

/**
 * Models a tweet ("status") as considered by this app. Status is used here over tweet to avoid
 * colliding with the Twitter API object [com.twitter.sdk.android.core.models.Tweet].
 * @param message Is the body of the message to be sent
 * @param photo is an optional photo to attach to the tweet
 */
data class Status(val message: String,
                  val photo: File? = null)
