package org.cru.godtools.tool.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FlowWatcher private constructor(private val job: Job) {
    internal companion object {
        internal fun <T> Flow<T>.watch(block: (T) -> Unit) = watchIn(CoroutineScope(Dispatchers.Main), block)
        private fun <T> Flow<T>.watchIn(scope: CoroutineScope, block: (T) -> Unit) =
            FlowWatcher(onEach { block(it) }.launchIn(scope))
    }

    fun close() = job.cancel()
}
