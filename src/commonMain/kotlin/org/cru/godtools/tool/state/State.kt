package org.cru.godtools.tool.state

import org.cru.godtools.tool.internal.Parcelable
import org.cru.godtools.tool.internal.Parcelize

@Parcelize
class State internal constructor(private val state: MutableMap<String, List<String>>) : Parcelable {
    constructor() : this(mutableMapOf<String, List<String>>())

    internal operator fun get(key: String) = state[key]?.firstOrNull()
    internal fun getAll(key: String) = state[key].orEmpty()

    internal operator fun set(key: String, value: String?) = state.set(key, listOfNotNull(value))
    internal operator fun set(key: String, values: List<String>?) = state.set(key, values.orEmpty())
    internal fun set(key: String, vararg values: String) = state.set(key, values.toList())
}
