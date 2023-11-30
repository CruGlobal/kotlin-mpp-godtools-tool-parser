package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.common.model.toUriOrNull
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.withDeviceType

@RunOnAndroidWith(AndroidJUnit4::class)
class ImageTest : UsesResources() {
    // region parse Image
    @Test
    fun testParseImageDefaults() = runTest {
        val image = Image(Manifest(), getTestXmlParser("image.xml"))
        assertEquals("image.png", image.resourceName)
        assertEquals(0, image.events.size)
        assertNull(image.url)
        assertEquals(Image.DEFAULT_WIDTH, image.width)
        assertEquals(Image.DEFAULT_GRAVITY, image.gravity)
    }

    @Test
    fun testParseImageAttrs() = runTest {
        val image = Image(Manifest(), getTestXmlParser("image_attrs.xml"))
        assertEquals("image.png", image.resourceName)
        assertEquals(2, image.events.size)
        assertEquals("event1", image.events[0].toString())
        assertEquals("event2", image.events[1].toString())
        assertEquals("https://example.com".toUriOrNull(), assertNotNull(image.url))
        assertEquals(Dimension.Pixels(50), image.width)
        assertEquals(Gravity.Horizontal.START, image.gravity)
    }

    @Test
    fun testParseImageWidthFull() = runTest {
        val image = Image(Manifest(), getTestXmlParser("image_width_full.xml"))
        assertEquals(Dimension.Percent(1f), image.width)
    }

    @Test
    fun testParseImageWidthInvalid() = runTest {
        val image = Image(Manifest(), getTestXmlParser("image_width_invalid.xml"))
        assertEquals(Image.DEFAULT_WIDTH, image.width)
    }

    @Test
    fun testParseImageRestricted() = runTest {
        val manifest = Manifest(
            config = ParserConfig().withDeviceType(DeviceType.IOS),
            resources = { listOf(Resource(it, "image.png")) }
        )
        val image = Image(manifest, getTestXmlParser("image_restricted.xml"))
        assertTrue(image.isIgnored)
    }
    // endregion parse Image

    // region Property: resource
    private val resourceName = "image.png"

    @Test
    fun testPropertyResource() {
        val manifest = Manifest(resources = { listOf(Resource(it, name = resourceName)) })
        assertSame(
            assertNotNull(manifest.getResource(resourceName)),
            assertNotNull(Image(manifest, resource = resourceName).resource),
        )
        assertNull(Image(manifest, resource = "invalid.jpg").resource)
        assertNull(Image(manifest, resource = null).resource)
    }

    @Test
    fun testPropertyResourceLegacyWebImages() {
        val config = ParserConfig().withLegacyWebImageResources(true)
        val manifest = Manifest(config = config, resources = { listOf(Resource(it, name = resourceName)) })
        assertSame(
            assertNotNull(manifest.getResource(resourceName)),
            assertNotNull(Image(manifest, resource = resourceName).resource),
        )
        assertNotNull(Image(manifest, resource = "invalid.jpg").resource) {
            assertEquals("invalid.jpg", it.name)
        }
        assertNull(Image(manifest, resource = null).resource)
    }
    // endregion Property: resource

    @Test
    fun testWidthAndGravity() {
        with(Image()) {
            assertEquals(Image.DEFAULT_WIDTH, width)
            assertEquals(Image.DEFAULT_GRAVITY, gravity)
        }

        with(Image(width = Dimension.Pixels(20), gravity = Gravity.Horizontal.END)) {
            assertEquals(Dimension.Pixels(20), width)
            assertEquals(Gravity.Horizontal.END, gravity)
        }
    }

    // region isIgnored
    @Test
    fun testIsIgnoredMissingResource() {
        val manifest = Manifest(resources = { listOf(Resource(it, "valid.png")) })
        assertTrue(Image(manifest, resource = null).isIgnored)
        assertTrue(Image(manifest, resource = "").isIgnored)
        assertFalse(Image(manifest, resource = "valid.png").isIgnored)
    }
    // endregion isIgnored
}
