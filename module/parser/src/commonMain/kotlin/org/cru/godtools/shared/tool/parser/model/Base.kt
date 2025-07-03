package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.RestrictTo
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
interface Base {
    @HiddenFromObjC
    @JsExport.Ignore
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val parent: Base? get() = null
    val manifest: Manifest get() = checkNotNull(parent?.manifest) { "No manifest found in model ancestors" }

    @HiddenFromObjC
    @JsExport.Ignore
    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val children: List<Base> get() = emptyList()
}

@HiddenFromObjC
@get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@OptIn(ExperimentalObjCRefinement::class)
val Base.descendants: List<Base>
    get() = buildList {
        children.forEach {
            add(it)
            addAll(it.descendants)
        }
    }

val Base?.stylesParent: Styles? get() = this?.parent?.let { it as? Styles ?: it.stylesParent }

internal fun Base.getResource(name: String?) = manifest.getResource(name)
