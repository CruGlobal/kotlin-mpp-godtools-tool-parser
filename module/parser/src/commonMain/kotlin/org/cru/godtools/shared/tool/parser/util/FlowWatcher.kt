package org.cru.godtools.shared.tool.parser.util

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@JsExport
@OptIn(ExperimentalJsExport::class)
class FlowWatcher private constructor(private val job: Job) {
    internal companion object {
        internal fun <T> Flow<T>.watch(block: (T) -> Unit) = watchIn(CoroutineScope(Dispatchers.Main), block)
        private fun <T> Flow<T>.watchIn(scope: CoroutineScope, block: (T) -> Unit) =
            FlowWatcher(onEach { block(it) }.launchIn(scope))
    }

    fun close() = job.cancel()
}
