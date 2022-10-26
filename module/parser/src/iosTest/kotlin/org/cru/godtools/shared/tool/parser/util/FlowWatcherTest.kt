package org.cru.godtools.shared.tool.parser.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.cru.godtools.shared.tool.parser.util.FlowWatcher.Companion.watch
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FlowWatcherTest {
    @Test
    fun testWatch() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        var latestValue = 0
        val source = MutableSharedFlow<Int>()
        val watcher = source.watch {
            latestValue = it
        }

        source.emit(1)
        assertEquals(1, latestValue)
        source.emit(2)
        assertEquals(2, latestValue)

        watcher.close()
        source.emit(3)
        assertEquals(2, latestValue)
    }
}
