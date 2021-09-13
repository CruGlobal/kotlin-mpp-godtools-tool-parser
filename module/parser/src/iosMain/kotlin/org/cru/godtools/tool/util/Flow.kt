package org.cru.godtools.tool.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

internal fun <T> Flow<T>.watch(block: (T) -> Unit) = watch(Dispatchers.Main, block)
internal fun <T> Flow<T>.watch(context: CoroutineContext, block: (T) -> Unit): FlowWatcher {
    val job = Job()

    onEach {
        block(it)
    }.launchIn(CoroutineScope(context + job))

    return FlowWatcher(job)
}

class FlowWatcher internal constructor(private val job: Job) {
    fun close() = job.cancel()
}