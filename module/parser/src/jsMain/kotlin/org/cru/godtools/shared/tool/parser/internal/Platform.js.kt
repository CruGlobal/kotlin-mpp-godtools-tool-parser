package org.cru.godtools.shared.tool.parser.internal

// region IOException
internal actual open class IOException actual constructor(message: String?) : Exception()
internal actual class FileNotFoundException actual constructor(message: String?) : IOException(message)
// endregion IOException
