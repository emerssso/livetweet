package com.emerssso.livetweet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.twitter.sdk.android.Twitter
import kotlinx.android.synthetic.main.activity_tweet.*

class TweetActivity : AppCompatActivity() {

    companion object {
        val MAX_UPDATE_LENGTH = 140
        val KEY_LAST_UPDATE_ID = "LAST_UPDATE_ID"
    }

    private var tweetSender = TweetSender(Twitter.getApiClient().statusesService)
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

        editPrepend.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                prependLength = charSequence.length
                setRemainingChars()
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        editAppend.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                appendLength = charSequence.length
                setRemainingChars()
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        editBody.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                bodyLength = charSequence.length
                setRemainingChars()
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
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
        when (item.itemId) {
            R.id.action_finish_talk -> {
                tweetSender = TweetSender(Twitter.getApiClient().statusesService)

                editPrepend.setText("")
                editBody.setText("")

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun sendTweet(view: View?) {
        val prepend = editPrepend.text.toString()
        val append = editAppend.text.toString()
        val body = editBody.text.toString()

        val message = StringUtils.buildMessage(prepend, body, append)

        if (StringUtils.isNonEmpty(message) && message.length <= MAX_UPDATE_LENGTH) {
            tweetSender.queueTweet(message)
            editBody.setText("")
            editBody.requestFocus()
        } else if (message.length > MAX_UPDATE_LENGTH) {
            Toast.makeText(this, R.string.tweet_too_long, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, R.string.update_empty, Toast.LENGTH_LONG).show()
        }
    }
}
