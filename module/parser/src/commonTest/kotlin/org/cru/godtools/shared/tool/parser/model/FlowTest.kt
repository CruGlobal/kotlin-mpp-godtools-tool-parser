package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Flow.Companion.DEFAULT_ITEM_WIDTH
import org.cru.godtools.shared.tool.parser.model.Flow.Companion.DEFAULT_ROW_GRAVITY
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip
import org.cru.godtools.shared.tool.state.State

@RunOnAndroidWith(AndroidJUnit4::class)
class FlowTest : UsesResources() {
    private val state = State()

    @Test
    fun testParseFlowDefaults() = runTest {
        val flow = Flow(Manifest(), getTestXmlParser("flow_defaults.xml"))
        assertEquals(DEFAULT_ITEM_WIDTH, flow.itemWidth)
        assertEquals(DEFAULT_ROW_GRAVITY, flow.rowGravity)
        assertTrue(flow.items.isEmpty())
        assertNull(flow.invisibleIf)
        assertNull(flow.goneIf)
    }

    @Test
    fun testParseFlow() = runTest {
        val flow = Flow(Manifest(), getTestXmlParser("flow.xml"))
        assertEquals(0.33f, assertIs<Dimension.Percent>(flow.itemWidth).value, DIMENSION_TOLERANCE)
        assertEquals(Gravity.Horizontal.END, flow.rowGravity)
        assertEquals(4, flow.items.size)
        assertIs<Spacer>(flow.items[0].content.single())
        with(flow.items[1]) {
            assertEquals(25, assertIs<Dimension.Pixels>(width).value)
            assertEquals(2, content.size)
            assertIs<Text>(content[0])
            assertIs<Spacer>(content[1])
        }
        assertIs<Paragraph>(flow.items[2].content.single())
        assertIs<InlineTip>(flow.items[3].content.single())
    }

    @Test
    fun testParseFlowItemVisibility() = runTest {
        val flow = Flow(Manifest(), getTestXmlParser("flow_visibility.xml"))
        assertEquals(1, flow.items.size)
        with(flow.items[0]) {
            assertFalse(isInvisible(state))
            state.setVar("invisible", listOf("true"))
            assertTrue(isInvisible(state))

            assertFalse(isGone(state))
            state.setVar("hidden", listOf("true"))
            assertTrue(isGone(state))
        }
    }

    @Test
    fun testParseFlowItemVisibilityInvalid() = runTest {
        val flow = Flow(Manifest(), getTestXmlParser("flow_visibility_invalid.xml"))
        assertEquals(1, flow.items.size)
        with(flow.items[0]) {
            assertNull(invisibleIf)
            assertFalse(isInvisible(state))

            assertNull(goneIf)
            assertFalse(isGone(state))
        }
    }

    @Test
    fun `Flow Parsing - Device Overrides - Web`() = runTest {
        val manifest = Manifest()
        if (manifest.config.deviceType != DeviceType.WEB) return@runTest

        val flow = Flow(manifest, getTestXmlParser("flow_device_overrides.xml"))
        assertEquals(Gravity.Horizontal.END, flow.rowGravity)
        assertEquals(Dimension.Percent(.25f), flow.items[0].width)
        assertEquals(Dimension.Pixels(25), flow.items[1].width)
    }

    @Test
    fun `Flow Parsing - Device Overrides - Not Web`() = runTest {
        val manifest = Manifest()
        if (manifest.config.deviceType == DeviceType.WEB) return@runTest

        val flow = Flow(manifest, getTestXmlParser("flow_device_overrides.xml"))
        assertEquals(Gravity.Horizontal.CENTER, flow.rowGravity)
        assertEquals(Dimension.Percent(.5f), flow.items[0].width)
        assertEquals(Dimension.Pixels(50), flow.items[1].width)
    }
}
