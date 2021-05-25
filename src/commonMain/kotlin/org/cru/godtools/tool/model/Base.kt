package org.cru.godtools.tool.model

interface Base {
    val stylesParent: Styles?
}

val Base?.stylesParent get() = this?.stylesParent
