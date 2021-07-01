package org.cru.godtools.tool.state

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onStart
import org.cru.godtools.tool.internal.Parcelable
import org.cru.godtools.tool.internal.Parcelize

@Parcelize
class State internal constructor(private val state: MutableMap<String, List<String>?>) : Parcelable {
    constructor() : this(mutableMapOf<String, List<String>?>())

    private val changeFlow = MutableSharedFlow<String>(extraBufferCapacity = Int.MAX_VALUE)
    internal fun changeFlow(key: String) = changeFlow.filter { it == key }.onStart { emit(key) }.conflate()

    internal operator fun get(key: String) = state[key]?.firstOrNull()
    internal fun getAll(key: String) = state[key].orEmpty()

    internal operator fun set(key: String, value: String?) = set(key, listOfNotNull(value))
    internal operator fun set(key: String, values: List<String>?) {
        state[key] = values
        changeFlow.tryEmit(key)
    }

    internal fun addValue(key: String, value: String) = set(key, (getAll(key) + value).distinct())
    internal fun removeValue(key: String, value: String) = set(key, getAll(key).filterNot { it == value })
}
