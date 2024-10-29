package org.cru.godtools.shared.tool.parser.model

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC
import kotlin.reflect.KClass
import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.tool.parser.model.page.Page

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
interface HasPages : Base {
    @JsName("_pages")
    val pages: List<Page>

    fun findPage(id: String?) = id?.let { pages.find { it.id == id } }

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictToScope.LIBRARY)
    fun <T : Page> supportsPageType(type: KClass<T>): Boolean

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("pages")
    val jsPages get() = pages.toTypedArray()
    // endregion Kotlin/JS interop
}
