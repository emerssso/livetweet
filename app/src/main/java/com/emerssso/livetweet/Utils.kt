package com.emerssso.livetweet

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.twitter.sdk.android.core.TwitterCore

val FIRST_TIME_USE = "com.emerssso.livetweet.FIRST_TIME_USE"

/**
 * Concatenates three strings into a message: "prepend body append",
 * with spaces only included if needed.
 * @param prepend String to be prepended, ignored if null/blank
 * *
 * @param body String to be concatenated to, ignored if null/blank
 * *
 * @param append String to be appended, ignored if null/blank
 * *
 * @return Result of the concatenation
 */
internal fun buildMessage(prepend: String?, body: String?, append: String?): String {
    return if(body.isFilled()) {
        val b = StringBuilder()

        if(prepend.isFilled()) b.append(prepend).append(" ")
        b.append(body)
        if(append.isFilled()) b.append(" ").append(append)

        b.toString()
    } else ""
}

internal fun String?.isFilled(): Boolean {
    return !this.isNullOrBlank()
}

internal fun getStatusesService() = TwitterCore.getInstance().apiClient.statusesService

internal fun getMediaService() = TwitterCore.getInstance().apiClient.mediaService

internal fun getSessionManager() = TwitterCore.getInstance().sessionManager

val EditText.content: String
    get() = text.toString()

internal fun EditText.onTextChanged(operation: (CharSequence?) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            operation(s)
        }

    })
}
