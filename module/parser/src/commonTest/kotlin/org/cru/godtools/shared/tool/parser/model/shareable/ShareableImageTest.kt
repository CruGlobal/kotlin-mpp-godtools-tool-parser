package org.cru.godtools.shared.tool.parser.model.shareable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Resource

@RunOnAndroidWith(AndroidJUnit4::class)
class ShareableImageTest : UsesResources("model/shareable") {
    // region ShareableImage XML
    @Test
    fun testParseShareableImage() = runTest {
        val image = Resource(name = "resource.jpg")
        with(ShareableImage(Manifest(resources = { listOf(image) }), getTestXmlParser("shareable_image.xml"))) {
            assertEquals("id", id)
            assertEquals(50, order)
            assertEquals("Description", description!!.text)
            assertSame(image, resource)
        }
    }
    // endregion ShareableImage XML

    // region Property - id
    @Test
    fun testIdBehavior() {
        val id = "id"
        val resource = "resource"
        assertNull(ShareableImage().id)
        assertEquals(id, ShareableImage(id = id).id)
        assertEquals(id, ShareableImage(id = id, resource = resource).id)
        assertEquals(resource, ShareableImage(resource = resource).id)
    }
    // endregion Property - id
}
