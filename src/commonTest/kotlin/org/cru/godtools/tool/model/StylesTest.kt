package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunOnAndroidWith(AndroidJUnit4::class)
class StylesTest {
    private val parent by lazy {
        object : Styles {
            override val stylesParent: Styles? = null
            override val manifest get() = TODO()

            override var primaryColor = TestColors.RED
            override var primaryTextColor = TestColors.RED
            override var textAlign = Text.Align.END
            override var textColor = TestColors.RED
            override var textScale = 0.0
        }
    }
    private val child by lazy { object : BaseModel(parent), Styles {} }

    @Test
    fun testStylesPrimaryColorFallback() {
        parent.primaryColor = TestColors.GREEN
        assertEquals(TestColors.GREEN, child.primaryColor)
        assertEquals(TestColors.GREEN, (child as Styles?).primaryColor)
    }

    @Test
    fun testStylesPrimaryTextColorFallback() {
        parent.primaryTextColor = TestColors.GREEN
        assertEquals(TestColors.GREEN, child.primaryTextColor)
        assertEquals(TestColors.GREEN, (child as Styles?).primaryTextColor)
    }

    @Test
    fun testStylesTextAlignFallback() {
        parent.textAlign = Text.Align.CENTER
        assertEquals(Text.Align.CENTER, child.textAlign)
        assertEquals(Text.Align.CENTER, (child as Styles?).textAlign)
    }

    @Test
    fun testStylesTextColorFallback() {
        parent.textColor = TestColors.GREEN
        assertEquals(TestColors.GREEN, child.textColor)
        assertEquals(TestColors.GREEN, (child as Styles?).textColor)
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
        assertEquals(Manifest.DEFAULT_TEXT_ALIGN, styles.textAlign)
        assertEquals(Manifest.DEFAULT_TEXT_COLOR, styles.textColor)
        assertEquals(Manifest.DEFAULT_TEXT_SCALE, styles.textScale)
    }
}
