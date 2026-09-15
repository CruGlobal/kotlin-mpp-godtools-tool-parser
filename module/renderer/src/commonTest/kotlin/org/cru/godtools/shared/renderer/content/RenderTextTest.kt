package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.height
import kotlin.test.Test
import kotlin.test.assertTrue
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.TestResources
import org.cru.godtools.shared.renderer.assertEquals
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text

private const val IMAGE_SIZE_SHORT = 8
private const val IMAGE_SIZE_TALL = 48

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RenderTextTest : BaseRenderContentTest() {
    private val manifest = Manifest(resources = { TestResources.resources })

    override val testModel = Text(manifest, text = "Test Text", invisibleIf = invisibleIf, goneIf = goneIf)
    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithText("Test Text")

    // region Image Align
    private fun imageAlignText(
        startImageAlign: Gravity.Vertical = Gravity.Vertical.CENTER,
        endImageAlign: Gravity.Vertical = Gravity.Vertical.CENTER,
        startImageSize: Int = IMAGE_SIZE_SHORT,
        endImageSize: Int = IMAGE_SIZE_SHORT,
        endImage: String? = "black_square",
    ) = Text(
        manifest,
        text = "Test Text",
        startImage = "black_square",
        startImageSize = startImageSize,
        startImageAlign = startImageAlign,
        endImage = endImage,
        endImageSize = endImageSize,
        endImageAlign = endImageAlign,
    )

    @Test
    fun `UI - image align - top aligns image tops with the text top`() = runComposeUiTest {
        renderContent(imageAlignText(startImageAlign = Gravity.Vertical.TOP, endImageAlign = Gravity.Vertical.TOP))

        val text = onNodeWithText("Test Text").getUnclippedBoundsInRoot()
        val start = onNodeWithTag(TestTagTextStartImage).getUnclippedBoundsInRoot()
        val end = onNodeWithTag(TestTagTextEndImage).getUnclippedBoundsInRoot()
        assertTrue(start.height < text.height, "the images should be shorter than the text for this test")
        assertEquals(text.top, start.top, "start image top should be flush with the text top")
        assertEquals(text.top, end.top, "end image top should be flush with the text top")
    }

    @Test
    fun `UI - image align - center centers images on the text midpoint`() = runComposeUiTest {
        renderContent(imageAlignText())

        val text = onNodeWithText("Test Text").getUnclippedBoundsInRoot()
        val start = onNodeWithTag(TestTagTextStartImage).getUnclippedBoundsInRoot()
        val end = onNodeWithTag(TestTagTextEndImage).getUnclippedBoundsInRoot()
        assertEquals(text.centerY, start.centerY, "start image should be centered on the text midpoint")
        assertEquals(text.centerY, end.centerY, "end image should be centered on the text midpoint")
    }

    @Test
    fun `UI - image align - bottom aligns image bottoms with the text bottom`() = runComposeUiTest {
        renderContent(
            imageAlignText(startImageAlign = Gravity.Vertical.BOTTOM, endImageAlign = Gravity.Vertical.BOTTOM)
        )

        val text = onNodeWithText("Test Text").getUnclippedBoundsInRoot()
        val start = onNodeWithTag(TestTagTextStartImage).getUnclippedBoundsInRoot()
        val end = onNodeWithTag(TestTagTextEndImage).getUnclippedBoundsInRoot()
        assertEquals(text.bottom, start.bottom, "start image bottom should be flush with the text bottom")
        assertEquals(text.bottom, end.bottom, "end image bottom should be flush with the text bottom")
    }

    @Test
    fun `UI - image align - mixed start and end aligns anchor to the text independently`() = runComposeUiTest {
        renderContent(imageAlignText(startImageAlign = Gravity.Vertical.TOP, endImageAlign = Gravity.Vertical.BOTTOM))

        val text = onNodeWithText("Test Text").getUnclippedBoundsInRoot()
        val start = onNodeWithTag(TestTagTextStartImage).getUnclippedBoundsInRoot()
        val end = onNodeWithTag(TestTagTextEndImage).getUnclippedBoundsInRoot()
        assertEquals(text.top, start.top, "start image top should be flush with the text top")
        assertEquals(text.bottom, end.bottom, "end image bottom should be flush with the text bottom")
    }

    @Test
    fun `UI - image align - taller top-aligned image expands the node below the text`() = runComposeUiTest {
        renderContent(
            imageAlignText(startImageSize = IMAGE_SIZE_TALL, startImageAlign = Gravity.Vertical.TOP, endImage = null),
            Text(manifest, text = "marker"),
        )

        val text = onNodeWithText("Test Text").getUnclippedBoundsInRoot()
        val start = onNodeWithTag(TestTagTextStartImage).getUnclippedBoundsInRoot()
        val marker = onNodeWithText("marker").getUnclippedBoundsInRoot()
        assertTrue(start.height > text.height, "the image should be taller than the text for this test")
        assertEquals(text.top, start.top, "image top should be flush with the text top")
        assertTrue(start.bottom > text.bottom, "a taller top-aligned image should extend below the text")
        assertEquals(start.bottom, marker.top, "the node should expand to fully contain the image")
    }

    @Test
    fun `UI - image align - taller bottom-aligned image expands the node above the text`() = runComposeUiTest {
        renderContent(
            imageAlignText(
                startImageSize = IMAGE_SIZE_TALL,
                startImageAlign = Gravity.Vertical.BOTTOM,
                endImage = null,
            ),
            Text(manifest, text = "marker"),
        )

        val root = onRoot().getUnclippedBoundsInRoot()
        val text = onNodeWithText("Test Text").getUnclippedBoundsInRoot()
        val start = onNodeWithTag(TestTagTextStartImage).getUnclippedBoundsInRoot()
        val marker = onNodeWithText("marker").getUnclippedBoundsInRoot()
        assertTrue(start.height > text.height, "the image should be taller than the text for this test")
        assertEquals(text.bottom, start.bottom, "image bottom should be flush with the text bottom")
        assertTrue(start.top < text.top, "a taller bottom-aligned image should extend above the text")
        assertEquals(root.top, start.top, "the node should expand to fully contain the image")
        assertEquals(text.bottom, marker.top, "the node should end at the shared text & image bottom")
    }

    @Test
    fun `UI - image align - mixed taller images expand the node to the union of extents`() = runComposeUiTest {
        renderContent(
            imageAlignText(
                startImageSize = IMAGE_SIZE_TALL,
                startImageAlign = Gravity.Vertical.TOP,
                endImageSize = IMAGE_SIZE_TALL,
                endImageAlign = Gravity.Vertical.BOTTOM,
            ),
            Text(manifest, text = "marker"),
        )

        val root = onRoot().getUnclippedBoundsInRoot()
        val text = onNodeWithText("Test Text").getUnclippedBoundsInRoot()
        val start = onNodeWithTag(TestTagTextStartImage).getUnclippedBoundsInRoot()
        val end = onNodeWithTag(TestTagTextEndImage).getUnclippedBoundsInRoot()
        val marker = onNodeWithText("marker").getUnclippedBoundsInRoot()
        assertTrue(start.height > text.height, "the images should be taller than the text for this test")
        assertEquals(text.top, start.top, "start image top should be flush with the text top")
        assertEquals(text.bottom, end.bottom, "end image bottom should be flush with the text bottom")
        assertEquals(root.top, end.top, "the node should expand upward to contain the end image")
        assertEquals(start.bottom, marker.top, "the node should expand downward to contain the start image")
    }
    // endregion Image Align
}

private val DpRect.centerY get() = (top + bottom) / 2
