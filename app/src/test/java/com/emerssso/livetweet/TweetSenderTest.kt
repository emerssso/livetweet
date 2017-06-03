package com.emerssso.livetweet

import android.graphics.Bitmap
import com.nhaarman.mockito_kotlin.*
import com.twitter.sdk.android.core.models.Media
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.MediaService
import com.twitter.sdk.android.core.services.StatusesService
import org.junit.Test
import org.mockito.Mockito.verify
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TweetSenderTest {
    companion object {
        private val TEST_1 = "test1"
        private val MEDIA_ID = 123L
    }

    val bitmap = mock<Bitmap>()
    val tweetCall = mock<Call<Tweet>>()
    val mediaCall = mock<Call<Media>>()
    val statusesService = mock<StatusesService> {
        on {update(TEST_1, null, false, null, null, null, null, true, null)} doReturn tweetCall
        on {update(TEST_1, null, false, null, null, null, null, true, "$MEDIA_ID")} doReturn tweetCall
    }
    val mediaService = mock<MediaService> {
        on {upload(any(), anyOrNull(), anyOrNull())} doReturn mediaCall
    }

    private val sut = TweetSender(statusesService, mediaService)

    @Test
    @Throws(Exception::class)
    fun shouldQueueIfEmpty() {
        sut.queueTweet(Status(TEST_1))

        verify(statusesService)
                .update(TEST_1, null, false, null, null, null, null, true, null)
        verify(tweetCall).enqueue(any())
    }

    @Test
    fun shouldSendMediaThenMessage() {
        sut.queueTweet(Status(TEST_1, bitmap))

        val media = Media(MEDIA_ID, "$MEDIA_ID", 5, null)
        val response = mock<Response<Media>> {
            on {isSuccessful} doReturn true
            on {body()} doReturn media
        }

        verify(mediaService).upload(any(), eq(null), eq(null))

        argumentCaptor<Callback<Media>>().apply {
            verify(mediaCall).enqueue(capture())

            firstValue.onResponse(mediaCall, response)
        }

        verify(statusesService)
                .update(TEST_1, null, false, null, null, null, null, true, "$MEDIA_ID")
        verify(tweetCall).enqueue(any())
    }
}
