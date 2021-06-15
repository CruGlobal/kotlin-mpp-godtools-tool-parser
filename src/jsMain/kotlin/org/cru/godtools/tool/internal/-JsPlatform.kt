package org.cru.godtools.tool.internal

// region IOException
internal actual open class IOException : Exception()
internal actual class FileNotFoundException : IOException()
// endregion IOException

actual interface Parcelable
