package org.cru.godtools.shared.tool.parser.model

import co.touchlab.kermit.Logger
import kotlin.jvm.JvmInline

@JvmInline
internal value class Version private constructor(val version: List<UInt>) : Comparable<Version> {
    companion object {
        internal val MIN = Version(emptyList())
        internal val MAX = Version(List(4) { UInt.MAX_VALUE })

        internal fun String.toVersion() = try {
            Version(split(".").map { it.toUInt() }.dropLastWhile { it == 0U })
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid version: $this", e)
        }

        internal fun String.toVersionOrNull() = try {
            toVersion()
        } catch (e: IllegalArgumentException) {
            Logger.e("Version", e) { "Invalid Version: $this" }
            null
        }
    }

    override fun compareTo(other: Version) =
        version.zip(other.version) { a, b -> a.compareTo(b) }.firstOrNull { it != 0 }
            ?: version.size.compareTo(other.version.size)

    override fun toString() = version.joinToString(".")
}
