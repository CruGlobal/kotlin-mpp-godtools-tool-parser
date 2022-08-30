package org.cru.godtools.tool.model

import kotlin.jvm.JvmInline

@JvmInline
internal value class Version private constructor(val version: List<UInt>) : Comparable<Version> {
    companion object {
        internal fun String.toVersion() = try {
            Version(split(".").map { it.toUInt() }.dropLastWhile { it == 0U })
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid version: $this", e)
        }
    }

    override fun compareTo(other: Version) =
        version.zip(other.version) { a, b -> a.compareTo(b) }.firstOrNull { it != 0 }
            ?: version.size.compareTo(other.version.size)

    override fun toString() = version.joinToString(".")
}
