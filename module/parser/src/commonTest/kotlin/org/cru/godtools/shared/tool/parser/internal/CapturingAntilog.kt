package org.cru.godtools.shared.tool.parser.internal

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

class CapturingAntilog : Antilog() {
    data class Log(val priority: LogLevel, val tag: String?, val throwable: Throwable?, val message: String?)

    val logsSent = mutableListOf<Log>()

    override fun performLog(priority: LogLevel, tag: String?, throwable: Throwable?, message: String?) {
        logsSent += Log(priority, tag, throwable, message)
    }
}
