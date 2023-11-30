package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_ANIMATION
import org.cru.godtools.shared.tool.parser.internal.UsesResources

@RunOnAndroidWith(AndroidJUnit4::class)
class AnimationTest : UsesResources() {
    @Test
    fun testParseAnimationDefaults() = runTest {
        val animation = Animation(Manifest(), getTestXmlParser("animation_defaults.xml"))
        assertEquals("animation.json", animation.resourceName)
        assertTrue(animation.autoPlay)
        assertTrue(animation.loop)
        assertTrue(animation.events.isEmpty())
        assertTrue(animation.playListeners.isEmpty())
        assertTrue(animation.stopListeners.isEmpty())
    }

    @Test
    fun testParseAnimation() = runTest {
        val animation = Animation(Manifest(), getTestXmlParser("animation.xml"))
        assertEquals("animation.json", animation.resourceName)
        assertFalse(animation.autoPlay)
        assertFalse(animation.loop)
        assertEquals(EventId.parse("event1 event2"), animation.events)
        assertEquals(EventId.parse("event1").toSet(), animation.playListeners)
        assertEquals(EventId.parse("event2").toSet(), animation.stopListeners)
    }

    @Test
    fun testIsIgnored() {
        with(Animation(Manifest(ParserConfig().withSupportedFeatures(FEATURE_ANIMATION)))) {
            assertFalse(isIgnored)
        }

        with(Animation(Manifest(ParserConfig().withSupportedFeatures()))) {
            assertTrue(isIgnored)
        }
    }
}
