package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip
import org.cru.godtools.shared.tool.parser.model.tips.Tip

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AccordionTest : UsesResources() {
    @Test
    fun testParseAccordion() = runTest {
        val accordion = Accordion(Manifest(), getTestXmlParser("accordion.xml"))
        assertEquals(2, accordion.sections.size)

        with(accordion.sections[0]) {
            assertEquals("Section 1", header!!.text)
            assertEquals(1, content.size)
            assertIs<Text>(content[0])
        }

        with(accordion.sections[1]) {
            assertEquals("Section 2", header!!.text)
            assertEquals(1, content.size)
            assertIs<Image>(content[0])
        }
    }

    @Test
    fun testParseAccordionAnalytics() = runTest {
        val accordion = Accordion(Manifest(), getTestXmlParser("accordion_analytics.xml"))
        assertEquals(1, accordion.sections.size)

        with(accordion.sections.single()) {
            val visible = getAnalyticsEvents(AnalyticsEvent.Trigger.VISIBLE)
            assertEquals(2, visible.size)
            assertEquals(setOf("default", "visible"), visible.map { it.action }.toSet())

            val hidden = getAnalyticsEvents(AnalyticsEvent.Trigger.HIDDEN)
            assertEquals(1, hidden.size)
            assertEquals("hidden", hidden.single().action)
        }
    }

    @Test
    fun testTipsProperty() {
        val manifest = Manifest(tips = { listOf(Tip(it, "tip1"), Tip(it, "tip2")) })
        val accordion = Accordion(manifest) {
            listOf(
                Accordion.Section(it) { listOf(InlineTip(it, "tip1")) },
                Accordion.Section(it) { listOf(InlineTip(it, "tip2")) },
            )
        }
        assertEquals(listOf(manifest.findTip("tip1")!!, manifest.findTip("tip2")!!), accordion.tips)
    }

    @Test
    fun testSectionAnalyticsEvents() {
        val section = Accordion.Section(
            analyticsEvents = listOf(
                AnalyticsEvent(action = "default", trigger = AnalyticsEvent.Trigger.DEFAULT),
                AnalyticsEvent(action = "visible", trigger = AnalyticsEvent.Trigger.VISIBLE),
                AnalyticsEvent(action = "hidden", trigger = AnalyticsEvent.Trigger.HIDDEN),
                AnalyticsEvent(action = "unknown", trigger = AnalyticsEvent.Trigger.UNKNOWN),
            )
        )

        with(section.getAnalyticsEvents(AnalyticsEvent.Trigger.VISIBLE)) {
            assertEquals(2, size)
            assertEquals(setOf("default", "visible"), map { it.action }.toSet())
        }

        with(section.getAnalyticsEvents(AnalyticsEvent.Trigger.HIDDEN)) {
            assertEquals(1, size)
            assertEquals("hidden", single().action)
        }

        assertFailsWith(IllegalStateException::class) { section.getAnalyticsEvents(AnalyticsEvent.Trigger.UNKNOWN) }
    }
}
