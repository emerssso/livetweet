package com.emerssso.livetweet

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.StatusesService
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import retrofit2.Call

class TweetSenderTest {
    companion object {
        private val TEST_1 = "test1"
    }

    @Mock internal val call = mock<Call<Tweet>> {}
    @Mock internal val statusesService = mock<StatusesService> {
        on {update(TEST_1, null, false, null, null, null, null, true, null)} doReturn call
    }

    private val sut = TweetSender(statusesService)

    @Test
    @Throws(Exception::class)
    fun shouldQueueIfEmpty() {
        sut.queueTweet(TEST_1)

        verify<StatusesService>(statusesService)
                .update(TEST_1, null, false, null, null, null, null, true, null)
        verify<Call<Tweet>>(call).enqueue(any())
    }
}
