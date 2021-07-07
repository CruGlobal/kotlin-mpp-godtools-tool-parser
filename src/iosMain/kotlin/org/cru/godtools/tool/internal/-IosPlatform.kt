package org.cru.godtools.tool.internal

// region IOException
internal actual open class IOException actual constructor(message: String?) : Exception()
internal actual class FileNotFoundException actual constructor(message: String?) : IOException(message)
// endregion IOException

actual interface Parcelable
