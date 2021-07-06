package org.cru.godtools.tool.internal

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout

suspend inline fun <T : Any?> Channel<T>.receive(timeout: Long) = withTimeout(timeout) { receive() }
