package org.cru.godtools.tool.model.shareable

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.Manifest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ShareableTest : UsesResources("model/shareable") {
    // region Shareable parsing
    @Test
    fun testParseShareableUnrecognized() = runTest {
        assertNull(Shareable.parse(Manifest(), getTestXmlParser("shareable_unrecognized.xml")))
    }

    @Test
    fun testParseShareableImage() = runTest {
        assertIs<ShareableImage>(Shareable.parse(Manifest(), getTestXmlParser("shareable_image.xml")))
    }
    // endregion Shareable parsing
}
