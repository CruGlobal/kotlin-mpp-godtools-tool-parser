package org.cru.godtools.shared.tool.parser.model

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.native.HiddenFromObjC
import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope

@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
interface Base {
    @HiddenFromObjC
    @JsExport.Ignore
    @get:RestrictTo(RestrictToScope.LIBRARY)
    val parent: Base? get() = null
    val manifest: Manifest
    val stylesParent: Styles?
}

val Base?.manifest get() = this?.manifest
val Base?.stylesParent get() = this?.stylesParent

internal fun Base.getResource(name: String?) = manifest.getResource(name)
