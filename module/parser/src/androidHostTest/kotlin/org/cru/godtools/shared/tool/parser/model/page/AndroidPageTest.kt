package org.cru.godtools.shared.tool.parser.model.page

import kotlin.test.Test
import kotlin.test.assertEquals
import org.cru.godtools.shared.tool.parser.model.ImageScaleType
import org.cru.godtools.shared.tool.parser.model.TEST_GRAVITY
import org.cru.godtools.shared.tool.parser.model.TestColors
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

class AndroidPageTest {
    @Test
    fun `Attribute - backgroundColor - Nullable Page`() {
        assertEquals(DEFAULT_BACKGROUND_COLOR, null.backgroundColor)

        val page: Page? = PageTest.TestPage(backgroundColor = TestColors.GREEN)
        assertEquals(TestColors.GREEN, page.backgroundColor)
    }

    @Test
    fun `Attribute - backgroundImageAttrs - Nullable Page`() {
        assertEquals(DEFAULT_BACKGROUND_IMAGE_GRAVITY, null.backgroundImageGravity)
        assertEquals(DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE, null.backgroundImageScaleType)

        val page: Page? = PageTest.TestPage(
            backgroundImageGravity = TEST_GRAVITY,
            backgroundImageScaleType = ImageScaleType.FIT
        )
        assertEquals(TEST_GRAVITY, page.backgroundImageGravity)
        assertEquals(ImageScaleType.FIT, page.backgroundImageScaleType)
    }

    @Test
    fun testNullablePageControlColor() {
        assertEquals(DEFAULT_CONTROL_COLOR, null.controlColor)

        val page: Page? = PageTest.TestPage(controlColor = TestColors.GREEN)
        assertEquals(TestColors.GREEN, page.controlColor)
    }
}
