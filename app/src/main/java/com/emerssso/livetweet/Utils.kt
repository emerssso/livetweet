package com.emerssso.livetweet

import com.twitter.sdk.android.core.TwitterCore

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
    if (!body.isNullOrBlank()) {
        if (!prepend.isNullOrBlank()) {
            if (!append.isNullOrBlank()) {
                return String.format("%s %s %s", prepend, body, append)
            } else {
                return String.format("%s %s", prepend, body)
            }
        } else {
            if (!append.isNullOrBlank()) {
                return String.format("%s %s", body, append)
            } else {
                return body ?: ""
            }
        }
    } else {
        return ""
    }
}

internal fun getStatusesService() = TwitterCore.getInstance().apiClient.statusesService
