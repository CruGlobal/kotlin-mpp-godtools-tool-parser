package org.cru.godtools.shared.renderer.state

import androidx.annotation.RestrictTo
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.native.HiddenFromObjC
import kotlinx.coroutines.flow.Flow

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
interface ExpressionContext {
    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun <T> varsChangeFlow(keys: Collection<String>? = emptyList(), block: (ExpressionContext) -> T): Flow<T>

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun getVar(key: String): List<String>

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun setVar(key: String, values: List<String>?)

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun addVarValue(key: String, value: String) {
        val values = getVar(key)
        if (!values.contains(value)) setVar(key, (values + value))
    }

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun removeVarValue(key: String, value: String) {
        val values = getVar(key)
        if (values.contains(value)) setVar(key, values.filterNot { it == value })
    }
}
