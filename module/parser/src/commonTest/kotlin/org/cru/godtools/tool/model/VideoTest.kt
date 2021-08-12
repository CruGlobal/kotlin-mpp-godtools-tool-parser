package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunOnAndroidWith(AndroidJUnit4::class)
class VideoTest : UsesResources() {
    @Test
    fun testParseVideo() = runBlockingTest {
        val video = Video(Manifest(), getTestXmlParser("video.xml"))
        assertEquals(Video.Provider.YOUTUBE, video.provider)
        assertEquals("test-id", video.videoId)
    }
}
