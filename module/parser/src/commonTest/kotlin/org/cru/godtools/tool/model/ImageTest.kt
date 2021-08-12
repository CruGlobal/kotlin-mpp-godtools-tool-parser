package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class ImageTest : UsesResources() {
    @Test
    fun testParseImage() = runBlockingTest {
        val image = Image(Manifest(), getTestXmlParser("image.xml"))
        assertEquals("image.png", image.resourceName)
        assertEquals(2, image.events.size)
        assertEquals("event1", image.events[0].toString())
        assertEquals("event2", image.events[1].toString())
    }

    @Test
    fun testParseImageRestricted() = runBlockingTest {
        val image = Image(Manifest(), getTestXmlParser("image_restricted.xml"))
        assertTrue(image.isIgnored)
    }

    @Test
    fun testResource() {
        val name = "image.png"
        val manifest = Manifest(resources = { listOf(Resource(it, name = name)) })
        val resource = manifest.getResource(name)!!
        assertSame(resource, Image(manifest, resource = "image.png").resource)
        assertNull(Image(manifest, resource = "invalid.jpg").resource)
        assertNull(Image(manifest, resource = null).resource)
    }
}
