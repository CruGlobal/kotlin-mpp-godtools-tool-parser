package org.cru.godtools.shared.tool.parser.model.shareable

import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Manifest

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
