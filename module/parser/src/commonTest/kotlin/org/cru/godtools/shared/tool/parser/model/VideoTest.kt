package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources

@RunOnAndroidWith(AndroidJUnit4::class)
class VideoTest : UsesResources() {
    @Test
    fun testParseVideo() = runTest {
        val video = Video(Manifest(), getTestXmlParser("video.xml"))
        assertEquals(Video.Provider.YOUTUBE, video.provider)
        assertEquals("test-id", video.videoId)
        assertEquals(AspectRatio(1, 1), video.aspectRatio)
        assertEquals(Dimension.Pixels(50), video.width)
        assertEquals(Gravity.Horizontal.END, video.gravity)
    }

    @Test
    fun testParseVideoDefaults() = runTest {
        val video = Video(Manifest(), getTestXmlParser("video_defaults.xml"))
        assertEquals(Video.Provider.YOUTUBE, video.provider)
        assertEquals("test-id", video.videoId)
        assertEquals(Video.DEFAULT_ASPECT_RATIO, video.aspectRatio)
        assertEquals(Video.DEFAULT_WIDTH, video.width)
        assertEquals(Video.DEFAULT_GRAVITY, video.gravity)
    }
}
