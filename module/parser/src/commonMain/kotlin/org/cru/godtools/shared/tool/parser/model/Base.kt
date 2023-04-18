package org.cru.godtools.shared.tool.parser.model

interface Base {
    val manifest: Manifest
    val stylesParent: Styles?
}

val Base?.manifest get() = this?.manifest
val Base?.stylesParent get() = this?.stylesParent

internal fun Base.getResource(name: String?) = manifest.getResource(name)
