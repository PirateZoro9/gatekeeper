package com.nen.gatekeeper

import junit.framework.TestCase.assertEquals
import org.junit.Test

class NavigationKeysSerializerTest {
    @Test
    fun homeSerializerExists() {
        val descriptor = Home.serializer().descriptor
        assertEquals("com.nen.gatekeeper.Home", descriptor.serialName)
        assertEquals(0, descriptor.elementsCount)
    }

    @Test
    fun detailSerializerExists() {
        val descriptor = Detail.serializer().descriptor
        assertEquals("com.nen.gatekeeper.Detail", descriptor.serialName)
        assertEquals(1, descriptor.elementsCount)
    }
}