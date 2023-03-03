package org.cru.godtools.shared.tool.state

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.tool.state.internal.Parcelable
import org.cru.godtools.shared.tool.state.internal.Parcelize
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Parcelize
@OptIn(ExperimentalObjCRefinement::class)
class State internal constructor(
    private val vars: MutableMap<String, List<String>?>
) : Parcelable {
    constructor() : this(vars = mutableMapOf<String, List<String>?>())

    // region State vars
    private val varsChangeFlow = MutableSharedFlow<String>(extraBufferCapacity = Int.MAX_VALUE)
    @HiddenFromObjC
    @RestrictTo(RestrictToScope.LIBRARY_GROUP)
    fun <T> varsChangeFlow(vararg key: String, block: (State) -> T) = varsChangeFlow(listOf(*key), block)
    @HiddenFromObjC
    @RestrictTo(RestrictToScope.LIBRARY_GROUP)
    fun <T> varsChangeFlow(keys: Collection<String>?, block: (State) -> T) = when {
        keys.isNullOrEmpty() -> flowOf(Unit)
        else -> varsChangeFlow.filter { it in keys }.map {}.onStart { emit(Unit) }.conflate()
    }.map { block(this) }

    fun getAll(key: String) = vars[key].orEmpty()

    operator fun set(key: String, value: String?) = set(key, listOfNotNull(value))
    operator fun set(key: String, values: List<String>?) {
        vars[key] = values?.toList()
        varsChangeFlow.tryEmit(key)
    }

    @HiddenFromObjC
    @RestrictTo(RestrictToScope.LIBRARY_GROUP)
    fun addVarValue(key: String, value: String) {
        val values = getAll(key)
        if (!values.contains(value)) set(key, (values + value))
    }
    @HiddenFromObjC
    @RestrictTo(RestrictToScope.LIBRARY_GROUP)
    fun removeVarValue(key: String, value: String) {
        val values = getAll(key)
        if (values.contains(value)) set(key, values.filterNot { it == value })
    }
    // endregion State vars
}
