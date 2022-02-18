package org.cru.godtools.tool.model

@Deprecated("This is provided to aid in iOS migration")
val Manifest?.resources get() = this?.resources.orEmpty()
