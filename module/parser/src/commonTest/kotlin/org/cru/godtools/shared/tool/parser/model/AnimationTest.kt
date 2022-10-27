package org.cru.godtools.shared.tool.parser.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_ANIMATION
import org.cru.godtools.shared.tool.parser.internal.AndroidJUnit4
import org.cru.godtools.shared.tool.parser.internal.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
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
        with(Animation(Manifest(ParserConfig().withSupportedFeatures(setOf(FEATURE_ANIMATION))))) {
            assertFalse(isIgnored)
        }

        with(Animation(Manifest(ParserConfig().withSupportedFeatures(emptySet())))) {
            assertTrue(isIgnored)
        }
    }
}
