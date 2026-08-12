package org.cru.godtools.shared.renderer.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
class ProvideResumedLifecycleOwnerTest : BaseRendererTest() {
    @Test
    fun `Lifecycle - content - only resumed while resumed=true`() = runComposeUiTest {
        var resumed by mutableStateOf(false)
        var resumeCount = 0
        var pauseCount = 0

        setContent {
            ProvideTestCompositionLocals {
                ProvideResumedLifecycleOwner(resumed = resumed) {
                    LifecycleResumeEffect(Unit) {
                        resumeCount++
                        onPauseOrDispose { pauseCount++ }
                    }
                }
            }
        }

        assertEquals(0, resumeCount)

        resumed = true
        waitForIdle()
        assertEquals(1, resumeCount)
        assertEquals(0, pauseCount)

        resumed = false
        waitForIdle()
        assertEquals(1, resumeCount)
        assertEquals(1, pauseCount)
    }
}
