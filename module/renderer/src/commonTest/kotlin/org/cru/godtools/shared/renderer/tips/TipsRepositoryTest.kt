package org.cru.godtools.shared.renderer.tips

import app.cash.turbine.test
import io.fluidsonic.locale.Locale
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

abstract class TipsRepositoryTest {
    abstract val repository: TipsRepository

    private val tool = "testTool"
    private val locale = Locale.forLanguage("en")
    private val tipId = "tip1"

    @Test
    fun `isTipCompleteFlow - emits true after marking complete`() = runTest {
        repository.isTipCompleteFlow(tool, locale, tipId).test {
            assertFalse(awaitItem())
            repository.markTipComplete(tool, locale, tipId)
            assertTrue(awaitItem())
        }
    }
}
