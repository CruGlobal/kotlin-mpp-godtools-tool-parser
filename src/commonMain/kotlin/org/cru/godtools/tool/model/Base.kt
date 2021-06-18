package org.cru.godtools.tool.model

interface Base {
    val manifest: Manifest
    val stylesParent: Styles?
}

val Base?.manifest get() = this?.manifest
val Base?.stylesParent get() = this?.stylesParent
