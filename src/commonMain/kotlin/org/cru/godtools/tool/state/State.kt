package org.cru.godtools.tool.state

class State internal constructor() {
    private val state = mutableMapOf<String, List<String>>()

    operator fun get(key: String) = state[key]?.firstOrNull()
    fun getAll(key: String) = state[key].orEmpty()

    operator fun set(key: String, value: String?) = state.set(key, listOfNotNull(value))
    operator fun set(key: String, values: List<String>?) = state.set(key, values.orEmpty())
    fun set(key: String, vararg values: String) = state.set(key, values.toList())
}
