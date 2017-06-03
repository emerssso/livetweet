package com.emerssso.livetweet

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_tweet.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.jetbrains.anko.warn


class TweetActivity : AppCompatActivity(), AnkoLogger {

    companion object {
        val MAX_UPDATE_LENGTH = 140
        val KEY_LAST_UPDATE_ID = "LAST_UPDATE_ID"
        val REQUEST_PHOTO = 1
    }

    private var tweetSender = TweetSender(getStatusesService(), getMediaService())
    private var prependLength = 0
    private var appendLength = 0
    private var bodyLength = 0
    private var photo: Bitmap? = null

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_tweet_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_finish_talk -> {
                startNewThread()
                return true
            }
            R.id.action_add_photo -> {
                selectPhoto()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_PHOTO) {
            when(resultCode) {
                RESULT_OK -> {
                    if(data != null) {
                        val inputStream = contentResolver.openInputStream(data.data)
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        attachedPhoto.setImageBitmap(bitmap)
                        attachedPhoto.visibility = View.VISIBLE

                        photo = bitmap
                    } else {
                        toast(R.string.photo_attachment_failed)
                        warn("no photo included in result")
                    }
                }
                else -> {
                    toast(R.string.photo_attachment_failed)
                    warn("result code was $resultCode")
                }
            }
        }
        else super.onActivityResult(requestCode, resultCode, data)
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

    private fun startNewThread() {
        tweetSender = TweetSender(getStatusesService(), getMediaService())

        editPrepend.setText("")
        editBody.setText("")
    }

    private fun selectPhoto() {
        val pickIntent = Intent()
        pickIntent.type = "image/*"
        pickIntent.action = Intent.ACTION_GET_CONTENT

        val chooserIntent = Intent.createChooser(pickIntent, getString(R.string.get_photo))

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                arrayOf(Intent(MediaStore.ACTION_IMAGE_CAPTURE)))

        startActivityForResult(chooserIntent, REQUEST_PHOTO)
    }

    fun sendTweet(@Suppress("UNUSED_PARAMETER") view: View?) {
        val message = buildMessage(editPrepend.content, editBody.content, editAppend.content)

        when {
            message.length > MAX_UPDATE_LENGTH -> toast(R.string.tweet_too_long)
            message.isBlank() -> toast(R.string.update_empty)
            else -> {
                tweetSender.queueTweet(Status(message, photo))

                editBody.setText("")
                editBody.requestFocus()
                photo = null
                attachedPhoto.visibility = View.GONE
            }
        }
    }
}
