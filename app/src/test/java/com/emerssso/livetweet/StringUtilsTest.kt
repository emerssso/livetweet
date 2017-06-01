package com.emerssso.livetweet

import org.junit.Assert.assertEquals
import org.junit.Test

class StringUtilsTest {

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
