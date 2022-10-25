package org.cru.godtools.tool.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.cru.godtools.tool.util.FlowWatcher.Companion.watch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FlowWatcherTest {
    @Test
    fun testWatch() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val channel = Channel<Int>(1)
        val output = Channel<Int>(1)
        val watcher = channel.consumeAsFlow().watch {
            output.trySend(it).getOrThrow()
        }

        channel.send(1)
        assertEquals(1, output.receive(1000))
        channel.send(2)
        assertEquals(2, output.receive(1000))

        watcher.close()
        channel.send(3)
        delay(1000)
        assertTrue(output.isEmpty)
    }
}
