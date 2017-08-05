package com.emerssso.livetweet

import com.nhaarman.mockito_kotlin.*
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Media
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.MediaService
import com.twitter.sdk.android.core.services.StatusesService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class TweetSenderTest {
    companion object {
        private val TEST_1 = "test1"
        private val MEDIA_ID = 123L
    }

    val file = mock<File>()
    val tweetCall = mock<Call<Tweet>>()
    val mediaCall = mock<Call<Media>>()
    val statusesService = mock<StatusesService> {
        on {update(TEST_1, null, false, null, null, null, null, true, null)} doReturn tweetCall
        on {update(TEST_1, null, false, null, null, null, null, true, "$MEDIA_ID")} doReturn tweetCall
    }
    val mediaService = mock<MediaService> {
        on {upload(any(), anyOrNull(), anyOrNull())} doReturn mediaCall
    }
    val failureCallback = mock<TweetSender.FailureCallback>()
    val exception = TwitterException("This is a test!")

    private val sut = TweetSender(statusesService, mediaService)

    @Test
    fun shouldQueueIfEmpty() {
        sut.queueTweet(Status(TEST_1))

        verify(statusesService)
                .update(TEST_1, null, false, null, null, null, null, true, null)
        verify(tweetCall).enqueue(any())
    }

    @Test
    fun shouldSendMediaThenMessage() {
        sut.queueTweet(Status(TEST_1, file))

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

    @Test
    fun shouldNotifyCallbacksOnFailure() {
        whenever(tweetCall.enqueue(any())).thenAnswer { invocation ->
            val callback = invocation?.getArgument<com.twitter.sdk.android.core.Callback<Tweet>>(0)

            callback?.failure(exception)
        }

        sut.registerCallback(failureCallback)

        sut.queueTweet(Status(TEST_1))

        verify(failureCallback).onFailure(exception)
    }
}
