package com.emerssso.livetweet;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Utility methods for string manipulation
 */
class StringUtils {

    /**
     * Concatenates three strings into a message: "prepend body append",
     * with spaces only included if needed.
     * @param prepend String to be prepended, ignored if null/blank
     * @param body String to be concatenated to, ignored if null/blank
     * @param append String to be appended, ignored if null/blank
     * @return Result of the concatenation
     */
    static @NonNull String buildMessage(@Nullable String prepend,
                                        @Nullable String body, @Nullable String append) {
        if (isNonEmpty(body)) {
            if (isNonEmpty(prepend)) {
                if (isNonEmpty(append)) {
                    return String.format("%s %s %s", prepend, body, append);
                } else {
                    return String.format("%s %s", prepend, body);
                }
            } else {
                if (isNonEmpty(append)) {
                    return String.format("%s %s", body, append);
                } else {
                    // we know body is non-empty at this point, 
                    // but IntelliJ's static analysis misses it
                    //noinspection ConstantConditions
                    return body;
                }
            }
        } else {
            return "";
        }
    }

    /**
     * Returns true only if the string contains 1 non-whitespace character, as defined by
     * Character#isWhitespace()
     *
     * based on Apache Commons StringUtils.isBlank()
     * https://commons.apache.org/proper/commons-lang/javadocs/api-2.6/org/apache/commons/lang/StringUtils.html#isBlank(java.lang.String)
     *
     * @param str string to check
     * @return true if at least one non-whitespace character, else false
     */
    static boolean isNonEmpty(@Nullable String str) {
        int strLen;

        if (str == null || (strLen = str.length()) == 0) {
            return false;
        }

        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }
}