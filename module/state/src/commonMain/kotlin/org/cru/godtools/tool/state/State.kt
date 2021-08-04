package org.cru.godtools.tool.state

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import org.cru.godtools.tool.internal.Parcelable
import org.cru.godtools.tool.internal.Parcelize

@Parcelize
class State internal constructor(private val state: MutableMap<String, List<String>?>) : Parcelable {
    constructor() : this(mutableMapOf<String, List<String>?>())

    private val changeFlow = MutableSharedFlow<String>(extraBufferCapacity = Int.MAX_VALUE)
    fun changeFlow(vararg key: String) = changeFlow.filter { it in key }.map {}.onStart { emit(Unit) }.conflate()

    operator fun get(key: String) = state[key]?.firstOrNull()
    fun getAll(key: String) = state[key].orEmpty()

    operator fun set(key: String, value: String?) = set(key, listOfNotNull(value))
    operator fun set(key: String, values: List<String>?) {
        state[key] = values?.toList()
        changeFlow.tryEmit(key)
    }

    fun addValue(key: String, value: String) {
        val values = getAll(key)
        if (!values.contains(value)) set(key, (values + value))
    }
    fun removeValue(key: String, value: String) {
        val values = getAll(key)
        if (values.contains(value)) set(key, values.filterNot { it == value })
    }
}
