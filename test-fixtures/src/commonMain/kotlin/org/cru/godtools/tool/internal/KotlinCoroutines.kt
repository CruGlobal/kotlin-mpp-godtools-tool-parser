package org.cru.godtools.tool.internal

import kotlinx.coroutines.CoroutineScope

// Copied w/ modifications from: https://github.com/Kotlin/kotlinx.coroutines/issues/1996#issuecomment-728562784
expect fun runBlockingTest(block: suspend CoroutineScope.() -> Unit)
