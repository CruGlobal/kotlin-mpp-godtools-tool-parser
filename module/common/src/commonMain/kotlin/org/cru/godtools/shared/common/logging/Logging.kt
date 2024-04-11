package org.cru.godtools.shared.common.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity

fun enableCustomLogging(onLog: (level: LogLevel, tag: String?, throwable: Throwable?, message: String?) -> Unit) {
    Logger.setLogWriters(
        object : LogWriter() {
            override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
                onLog(
                    when (severity) {
                        Severity.Verbose -> LogLevel.VERBOSE
                        Severity.Debug -> LogLevel.DEBUG
                        Severity.Info -> LogLevel.INFO
                        Severity.Warn -> LogLevel.WARN
                        Severity.Error -> LogLevel.ERROR
                        Severity.Assert -> LogLevel.ASSERT
                    },
                    tag,
                    throwable,
                    message,
                )
            }
        }
    )
}

enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    ASSERT
}
