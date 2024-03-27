package org.cru.godtools.shared.tool.parser.internal

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity

class CapturingLogWriter : LogWriter() {
    data class Log(val severity: Severity, val tag: String, val message: String, val throwable: Throwable?)

    val logsSent = mutableListOf<Log>()

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        logsSent += Log(severity, tag = tag, throwable = throwable, message = message)
    }
}
