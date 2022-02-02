package org.cru.godtools.tool.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.Test
import kotlin.test.assertEquals

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
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
