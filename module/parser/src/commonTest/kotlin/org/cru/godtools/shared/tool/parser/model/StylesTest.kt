package org.cru.godtools.shared.tool.parser.model

import com.github.ajalt.colormath.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StylesTest {
    private val parent by lazy {
        object : Styles {
            override var primaryColor = TestColors.RED
            override var primaryTextColor = TestColors.RED

            override lateinit var buttonStyle: Button.Style

            override var multiselectOptionBackgroundColor = TestColors.random()
            override var multiselectOptionSelectedColor: Color? = null

            override var textAlign = Text.Align.END
            override var textColor = TestColors.RED
            override var textScale = 0.0
        }
    }
    private val child by lazy { object : BaseModel(parent), Styles {} }

    @Test
    fun testStylesPrimaryColorFallback() {
        parent.primaryColor = TestColors.random()
        assertEquals(parent.primaryColor, child.primaryColor)
        assertEquals(parent.primaryColor, (child as Styles?).primaryColor)
    }

    @Test
    fun testStylesPrimaryTextColorFallback() {
        parent.primaryTextColor = TestColors.random()
        assertEquals(parent.primaryTextColor, child.primaryTextColor)
        assertEquals(parent.primaryTextColor, (child as Styles?).primaryTextColor)
    }

    @Test
    fun testStylesButtonStyleFallback() {
        parent.buttonStyle = Button.Style.entries.random()
        assertEquals(parent.buttonStyle, child.buttonStyle)
        assertEquals(parent.buttonStyle, (child as Styles?).buttonStyle)
    }

    @Test
    fun testStylesMultiselectOptionBackgroundColorFallback() {
        assertEquals(parent.multiselectOptionBackgroundColor, child.multiselectOptionBackgroundColor)
        assertEquals(parent.multiselectOptionBackgroundColor, (child as Styles?).multiselectOptionBackgroundColor)
    }

    @Test
    fun testStylesMultiselectOptionSelectedColorFallback() {
        parent.multiselectOptionSelectedColor = null
        assertNull(child.multiselectOptionSelectedColor)
        parent.multiselectOptionSelectedColor = TestColors.random()
        assertEquals(parent.multiselectOptionSelectedColor, child.multiselectOptionSelectedColor)
    }

    @Test
    fun testStylesTextAlignFallback() {
        parent.textAlign = Text.Align.entries.random()
        assertEquals(parent.textAlign, child.textAlign)
        assertEquals(parent.textAlign, (child as Styles?).textAlign)
    }

    @Test
    fun testStylesTextColorFallback() {
        parent.textColor = TestColors.random()
        assertEquals(parent.textColor, child.textColor)
        assertEquals(parent.textColor, (child as Styles?).textColor)
    }

    @Test
    fun testStylesTextScaleFallback() {
        parent.textScale = 5.0
        assertEquals(5.0, child.textScale, 0.0001)
        assertEquals(5.0, (child as Styles?).textScale, 0.0001)
    }

    @Test
    fun testStylesDefaultsForNull() {
        val styles: Styles? = null
        assertEquals(Manifest.DEFAULT_PRIMARY_COLOR, styles.primaryColor)
        assertEquals(Manifest.DEFAULT_PRIMARY_TEXT_COLOR, styles.primaryTextColor)
        assertEquals(Manifest.DEFAULT_BUTTON_STYLE, styles.buttonStyle)
        assertEquals(Manifest.DEFAULT_BACKGROUND_COLOR, styles.multiselectOptionBackgroundColor)
        assertEquals(Styles.DEFAULT_TEXT_ALIGN, styles.textAlign)
        assertEquals(Manifest.DEFAULT_TEXT_COLOR, styles.textColor)
        assertEquals(Styles.DEFAULT_TEXT_SCALE, styles.textScale)
    }
}
