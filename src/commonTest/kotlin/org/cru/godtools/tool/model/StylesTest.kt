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

            override var primaryColor: Color = TestColors.RED
            override var primaryTextColor: Color = TestColors.RED
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
    fun testStylesDefaultsForNull() {
        val styles: Styles? = null
        assertEquals(Manifest.DEFAULT_PRIMARY_COLOR, styles.primaryColor)
        assertEquals(Manifest.DEFAULT_PRIMARY_TEXT_COLOR, styles.primaryTextColor)
    }
}