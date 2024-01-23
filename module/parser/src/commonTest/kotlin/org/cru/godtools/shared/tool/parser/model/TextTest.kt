package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.expressions.toExpressionOrNull
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.shared.tool.parser.model.Text.Align.Companion.toTextAlignOrNull
import org.cru.godtools.shared.tool.parser.model.Text.Style.Companion.toTextStyles

@RunOnAndroidWith(AndroidJUnit4::class)
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
    fun testPropertyTextColor() {
        assertEquals(TestColors.GREEN, Text(Manifest(textColor = TestColors.GREEN)).textColor)
        assertEquals(
            TestColors.GREEN,
            Text(Manifest(textColor = TestColors.RED), textColor = TestColors.GREEN).textColor,
        )
    }

    @Test
    fun testPropertyTextScale() {
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
    fun testEquals() {
        assertEquals(Text(textScale = 1.3), Text(textScale = 1.3))

        val text = Text()
        assertEquals(text, text)
        assertFalse(Text().equals(null))
        assertFalse(Text(text = "text").equals("text"))
        assertNotEquals(Text(text = "first"), Text(text = "second"))
        assertNotEquals(Text(textAlign = Text.Align.START), Text(textAlign = Text.Align.END))
        assertNotEquals(Text(textColor = TestColors.BLACK), Text(textColor = TestColors.GREEN))
        assertNotEquals(Text(textScale = 1.0), Text(textScale = 2.0))
        assertNotEquals(Text(textStyles = setOf(Text.Style.BOLD)), Text(textStyles = setOf(Text.Style.ITALIC)))
        assertNotEquals(Text(minimumLines = 1), Text(minimumLines = 2))
        assertNotEquals(Text(startImageSize = 30), Text(startImageSize = 40))
        assertNotEquals(Text(endImageSize = 30), Text(endImageSize = 40))
    }

    @Test
    fun testEqualsStartEndImages() {
        val manifest = Manifest(resources = { listOf(Resource(name = "1.jpg"), Resource(name = "2.jpg")) })

        assertEquals(Text(manifest, startImage = null), Text(manifest, startImage = "invalid.jpg"))
        assertNotEquals(Text(manifest, startImage = "1.jpg"), Text(manifest, startImage = "2.jpg"))
        assertNotEquals(Text(manifest, startImage = null), Text(manifest, startImage = "2.jpg"))

        assertEquals(Text(manifest, endImage = null), Text(manifest, endImage = "invalid.jpg"))
        assertNotEquals(Text(manifest, endImage = "1.jpg"), Text(manifest, endImage = "2.jpg"))
        assertNotEquals(Text(manifest, endImage = null), Text(manifest, endImage = "2.jpg"))
    }

    @Test
    fun testEqualsVisibilityExpressions() {
        val expr1 = "var='test'".toExpressionOrNull()
        val expr2 = "var='test'".toExpressionOrNull()
        assertEquals(Text(goneIf = expr1), Text(goneIf = expr2))
        assertEquals(Text(invisibleIf = expr1), Text(invisibleIf = expr2))

        val expr3 = "var2='test'".toExpressionOrNull()
        assertNotEquals(Text(goneIf = expr1), Text())
        assertNotEquals(Text(goneIf = expr1), Text(goneIf = expr3))
        assertNotEquals(Text(invisibleIf = expr1), Text())
        assertNotEquals(Text(invisibleIf = expr1), Text(invisibleIf = expr3))
    }

    @Test
    fun testHashCode() {
        assertEquals(Text(text = "text").hashCode(), Text(text = "text").hashCode())

        val manifest = Manifest(resources = { listOf(Resource(name = "1.jpg")) })
        assertEquals(
            Text(manifest, startImage = "1.jpg", endImage = "1.jpg").hashCode(),
            Text(manifest, startImage = "1.jpg", endImage = "1.jpg").hashCode(),
        )
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
