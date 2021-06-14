package org.cru.godtools.tool.model

interface Base {
    val stylesParent: Styles?
    val manifest: Manifest
}

val Base?.stylesParent get() = this?.stylesParent
internal val Base?.manifest get() = this?.manifest
