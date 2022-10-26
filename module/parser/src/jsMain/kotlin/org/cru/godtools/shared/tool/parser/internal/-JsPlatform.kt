package org.cru.godtools.shared.tool.parser.internal

// region Android Annotations
internal actual enum class RestrictToScope { LIBRARY, TESTS, SUBCLASSES }
// endregion Android Annotations

// region IOException
internal actual open class IOException actual constructor(message: String?) : Exception()
internal actual class FileNotFoundException actual constructor(message: String?) : IOException(message)
// endregion IOException
