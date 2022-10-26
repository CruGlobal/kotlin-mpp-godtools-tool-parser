package org.cru.godtools.shared.tool.parser.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private class SetOnceProperty<T> : ReadWriteProperty<Any, T> {
    private object EMPTY

    private var value: Any? = EMPTY

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        check(value !== EMPTY) { "Value isn't initialized" }
        return value as T
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        check(this.value === EMPTY) { "Value is initialized" }
        this.value = value
    }
}

internal fun <T> setOnce(): ReadWriteProperty<Any, T> = SetOnceProperty()
