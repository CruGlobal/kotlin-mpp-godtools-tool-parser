package org.cru.godtools.tool.util

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import org.cru.godtools.tool.internal.coroutines.receive
import org.cru.godtools.tool.internal.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowTest {
    @Test
    fun testWatch() = runBlockingTest {
        val channel = Channel<Int>(1)
        val output = Channel<Int>(1)
        val watcher = channel.consumeAsFlow().watch(coroutineContext) {
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