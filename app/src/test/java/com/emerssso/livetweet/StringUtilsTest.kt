package com.emerssso.livetweet

import com.emerssso.livetweet.StringUtils.buildMessage
import com.emerssso.livetweet.StringUtils.isNonEmpty
import org.junit.Assert.*
import org.junit.Test

class StringUtilsTest {

    @Test fun shouldReturnFalseForNull() {
        assertFalse(isNonEmpty(null))
    }

    @Test fun shouldReturnFalseForEmpty() {
        assertFalse(isNonEmpty(""))
    }

    @Test
    fun shouldReturnFalseForWhitespace() {
        assertFalse(isNonEmpty(" \n\t"))
    }

    @Test fun shouldReturnTrueForMixed() {
        assertTrue(isNonEmpty(" t"))
        assertTrue(isNonEmpty("t "))
    }

    @Test fun shouldReturnTrueForNonWhitespace() {
        assertTrue(isNonEmpty("t"))
    }

    @Test fun shouldReturnAll() {
        assertEquals("prepend body append", buildMessage("prepend", "body", "append"))
    }

    @Test fun shouldReturnPrependAndBody() {
        assertEquals("prepend body", buildMessage("prepend", "body", null))
        assertEquals("prepend body", buildMessage("prepend", "body", ""))
        assertEquals("prepend body", buildMessage("prepend", "body", " "))
    }

    @Test fun shouldReturnBodyAndAppend() {
        assertEquals("body append", buildMessage(null, "body", "append"))
        assertEquals("body append", buildMessage("", "body", "append"))
        assertEquals("body append", buildMessage(" ", "body", "append"))
    }

    @Test fun shouldReturnEmptyIfNoBody() {
        assertEquals("", buildMessage("prepend", null, "append"))
        assertEquals("", buildMessage("prepend", "", "append"))
        assertEquals("", buildMessage("prepend", " ", "append"))
        assertEquals("", buildMessage(null, null, null))
    }
}
