package com.emerssso.livetweet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_tweet.*
import org.jetbrains.anko.toast

class TweetActivity : AppCompatActivity() {

    companion object {
        val MAX_UPDATE_LENGTH = 140
        val KEY_LAST_UPDATE_ID = "LAST_UPDATE_ID"
    }

    private var tweetSender = TweetSender(getStatusesService())
    private var prependLength = 0
    private var appendLength = 0
    private var bodyLength = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweet)

        setSupportActionBar(toolbar)

        if (savedInstanceState != null) {
            tweetSender.lastId = savedInstanceState.getLong(KEY_LAST_UPDATE_ID)
        }

        editPrepend.onTextChanged {
            prependLength = it?.length ?: 0
            setRemainingChars()
        }

        editAppend.onTextChanged {
            appendLength = it?.length ?: 0
            setRemainingChars()
        }

        editBody.onTextChanged {
            bodyLength = it?.length ?: 0
            setRemainingChars()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val lastId = tweetSender.lastId
        if (lastId != null) {
            outState.putLong(KEY_LAST_UPDATE_ID, lastId)
        }

        super.onSaveInstanceState(outState)
    }

    private fun setRemainingChars() {
        var length = MAX_UPDATE_LENGTH

        length -= prependLength + bodyLength + appendLength
        if (prependLength > 0) {
            length--
        }

        if (appendLength > 0) {
            length--
        }

        toolbar.title = getString(R.string.send_next_length, length)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_tweet_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_finish_talk) {
            tweetSender = TweetSender(getStatusesService())

            editPrepend.setText("")
            editBody.setText("")

            return true
        }
        else return super.onOptionsItemSelected(item)
    }

    fun sendTweet(@Suppress("UNUSED_PARAMETER") view: View?) {
        val message = buildMessage(editPrepend.content, editBody.content, editAppend.content)

        when {
            message.length > MAX_UPDATE_LENGTH -> toast(R.string.tweet_too_long)
            message.isBlank() -> toast(R.string.update_empty)
            else -> {
                tweetSender.queueTweet(message)
                editBody.setText("")
                editBody.requestFocus()
            }
        }
    }
}
