package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class ImageTest : UsesResources() {
    @Test
    fun testParseImage() {
        val image = Image(Manifest(), getTestXmlParser("image.xml"))
        assertEquals("image.png", image.resourceName)
        assertEquals(2, image.events.size)
        assertEquals("event1", image.events[0].toString())
        assertEquals("event2", image.events[1].toString())
    }

    @Test
    fun testParseImageRestricted() {
        val image = Image(Manifest(), getTestXmlParser("image_restricted.xml"))
        assertTrue(image.isIgnored)
    }
}
