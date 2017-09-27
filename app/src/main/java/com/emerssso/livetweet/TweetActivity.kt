package com.emerssso.livetweet

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.core.TwitterException
import kotlinx.android.synthetic.main.activity_tweet.*
import org.jetbrains.anko.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class TweetActivity : ParentActivity(), AnkoLogger {

    private var tweetSender = TweetSender(statusesService, mediaService)
    private var prependLength = 0
    private var appendLength = 0
    private var bodyLength = 0
    private var photoFile: File? = null
    private var resumed = false
    private val callback = object : TweetSender.FailureCallback {
        override fun onFailure(exception: TwitterException?) {
            if(resumed) toast(getString(R.string.unable_to_send, exception?.message))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweet)

        setSupportActionBar(toolbar)

        tweetSender.lastId = savedInstanceState?.getLong(KEY_LAST_UPDATE_ID)

        val path = savedInstanceState?.getString(KEY_PHOTO_FILE)
        photoFile = if (path != null) File(path) else null
        if(photoFile != null) {
            showPhotoFile()
        }

        tweetSender.registerCallback(callback)

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

    override fun onResume() {
        super.onResume()
        resumed = true

        //don't proceed if privacy policy was bypassed
        if(getSharedPreferences(FIRST_TIME_USE, Context.MODE_PRIVATE)
                .getBoolean(FIRST_TIME_USE, true)) {
            toast(getString(R.string.policy_not_accepted))
            finish()
        }
    }

    override fun onPause() {
        resumed = false
        super.onPause()
    }

    override fun onDestroy() {
        tweetSender.unregisterCallback(callback)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val lastId = tweetSender.lastId
        if (lastId != null) {
            outState.putLong(KEY_LAST_UPDATE_ID, lastId)
        }

        if(photoFile != null) {
            outState.putString(KEY_PHOTO_FILE, photoFile?.absolutePath)
        }

        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_tweet_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_finish_talk -> {
                alert(R.string.finish_thread) {
                    yesButton { startNewThread() }
                    noButton {  }
                }.show()
                return true
            }
            R.id.action_add_photo -> {
                selectPhoto()
                return true
            }
            R.id.action_log_out -> {
                alert(getString(R.string.confirm_log_out)) {
                    positiveButton(R.string.log_out) {
                        sessionManager.clearActiveSession()
                        startActivity(intentFor<LoginActivity>())
                        finish()
                    }
                    noButton {  }
                }.show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PHOTO -> when (resultCode) {
                RESULT_OK -> {
                    info("loading $photoFile into screen")
                    showPhotoFile()
                }
                else -> {
                    toast(R.string.photo_attachment_failed)
                    warn("result code was $resultCode")
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showPhotoFile() {
        Picasso.with(this)
                .load(photoFile)
                .into(attachedPhoto)
        attachedPhoto.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_WRITE_PERMISSION) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPhoto()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        tweetSender = TweetSender(statusesService, mediaService)
        tweetSender.registerCallback(callback)

        editPrepend.setText("")
        editBody.setText("")
    }

    private fun selectPhoto() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

            photoFile = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg",
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))

            val photoUri = FileProvider.getUriForFile(this,
                    "com.emerssso.livetweet.fileprovider", photoFile)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

            startActivityForResult(cameraIntent, REQUEST_PHOTO)
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                toast(R.string.storage_perm_explanation)
            }

            ActivityCompat.requestPermissions(this,
                    listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray(),
                    REQUEST_WRITE_PERMISSION)
        }
    }

    fun sendTweet(@Suppress("UNUSED_PARAMETER") view: View?) {
        val message = buildMessage(editPrepend.content, editBody.content, editAppend.content)

        when {
            message.length > MAX_UPDATE_LENGTH -> toast(R.string.tweet_too_long)
            message.isBlank() -> toast(R.string.update_empty)
            else -> {
                tweetSender.queueTweet(Status(message, photoFile))

                editBody.setText("")
                editBody.requestFocus()
                photoFile = null
                attachedPhoto.visibility = View.GONE
            }
        }
    }
}
