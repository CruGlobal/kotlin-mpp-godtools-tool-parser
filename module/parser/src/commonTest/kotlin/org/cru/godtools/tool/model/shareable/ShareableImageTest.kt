package org.cru.godtools.tool.model.shareable

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Resource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ShareableImageTest : UsesResources("model/shareable") {
    // region ShareableImage XML
    @Test
    fun testParseShareableImage() = runTest {
        val image = Resource(name = "resource.jpg")
        with(ShareableImage(Manifest(resources = { listOf(image) }), getTestXmlParser("shareable_image.xml"))) {
            assertEquals("id", id)
            assertEquals("Description", description!!.text)
            assertSame(image, resource)
        }
    }
    // endregion ShareableImage XML
}
