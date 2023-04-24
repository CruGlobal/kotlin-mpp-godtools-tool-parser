package org.cru.godtools.shared.tool.parser.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.shared.tool.parser.model.Text.Align.Companion.toTextAlignOrNull
import org.cru.godtools.shared.tool.parser.model.Text.Style.Companion.toTextStyles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TextTest : UsesResources() {
    // region Parsing
    @Test
    fun testTextParsingDefaults() = runTest {
        val manifest = Manifest()
        val text = Text(manifest, getTestXmlParser("text_defaults.xml"))

        assertEquals("Text Defaults", text.text)
        assertEquals(DEFAULT_TEXT_SCALE * text.stylesParent.textScale, text.textScale, 0.001)
        assertEquals(Manifest.DEFAULT_TEXT_COLOR, text.textColor)
        assertEquals(manifest.textAlign, text.textAlign)
        assertEquals(Text.DEFAULT_MINIMUM_LINES, text.minimumLines)
        assertEquals(Text.DEFAULT_IMAGE_SIZE, text.startImageSize)
        assertEquals(Text.DEFAULT_IMAGE_SIZE, text.endImageSize)
        assertTrue(text.textStyles.isEmpty())
    }

    @Test
    fun testTextParsingAttributes() = runTest {
        val manifest = Manifest()
        val text = Text(manifest, getTestXmlParser("text_attributes.xml"))

        assertEquals("Attributes", text.text)
        assertEquals(1.23, text.textScale, 0.001)
        assertEquals(TestColors.GREEN, text.textColor)
        assertEquals(Text.Align.END, text.textAlign)
        assertNotEquals(manifest.textAlign, text.textAlign)
        assertEquals(5, text.minimumLines)
        assertEquals("start.png", text.startImageName)
        assertEquals(5, text.startImageSize)
        assertEquals("end.png", text.endImageName)
        assertEquals(11, text.endImageSize)
        assertEquals(setOf(Text.Style.BOLD, Text.Style.ITALIC), text.textStyles)
    }
    // endregion Parsing

    @Test
    fun testStartImage() {
        val name = "image.png"
        val manifest = Manifest(resources = { listOf(Resource(it, name = name)) })
        val resource = manifest.getResource(name)!!
        assertSame(resource, Text(manifest, startImage = "image.png").startImage)
        assertNull(Text(manifest, startImage = null).startImage)
    }

    @Test
    fun testEndImage() {
        val name = "image.png"
        val manifest = Manifest(resources = { listOf(Resource(it, name = name)) })
        val resource = manifest.getResource(name)!!
        assertSame(resource, Text(manifest, endImage = "image.png").endImage)
        assertNull(Text(manifest, endImage = null).endImage)
    }

    @Test
    fun testPropertyTextAlign() {
        val parent = Manifest().stylesOverride(textAlign = { Text.Align.CENTER })
        with(Text(parent = parent)) {
            assertEquals(Text.Align.CENTER, textAlign)
        }

        with(Text(parent = parent, textAlign = Text.Align.END)) {
            assertEquals(Text.Align.END, textAlign)
        }
    }

    @Test
    fun testTextScale() {
        with(Manifest().stylesOverride(textScale = DEFAULT_TEXT_SCALE)) {
            assertEquals(DEFAULT_TEXT_SCALE, Text(this).textScale, 0.001)
            assertEquals(2.0, Text(this, textScale = 2.0).textScale, 0.001)
        }

        with(Manifest().stylesOverride(textScale = 3.0)) {
            assertEquals(3.0, Text(this).textScale, 0.001)
            assertEquals(6.0, Text(this, textScale = 2.0).textScale, 0.001)
        }
    }

    @Test
    fun testTextAlignParsing() {
        assertEquals(Text.Align.START, "start".toTextAlignOrNull())
        assertEquals(Text.Align.CENTER, "center".toTextAlignOrNull())
        assertEquals(Text.Align.END, "end".toTextAlignOrNull())
        assertNull("jaksldf".toTextAlignOrNull())
    }

    @Test
    fun testTextStyleParsing() {
        assertEquals(setOf(Text.Style.BOLD, Text.Style.ITALIC), "bold italic".toTextStyles())
        assertEquals(setOf(Text.Style.UNDERLINE), "underline underline".toTextStyles())
        assertTrue("ajksdf jalkwer".toTextStyles().isEmpty())
    }
}
