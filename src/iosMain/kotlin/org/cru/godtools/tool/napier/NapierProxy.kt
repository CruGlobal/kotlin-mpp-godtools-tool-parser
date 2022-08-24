package org.cru.godtools.tool.napier

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

fun enableDebugLogging() {
    Napier.base(DebugAntilog())
}

fun enableCustomLogging(
    onLog: (priority: LogLevel, tag: String?, throwable: Throwable?, message: String?) -> Unit
) {
    Napier.base(object : Antilog() {
        override fun performLog(priority: LogLevel, tag: String?, throwable: Throwable?, message: String?) {
            onLog(priority, tag, throwable, message)
        }
    })
}
