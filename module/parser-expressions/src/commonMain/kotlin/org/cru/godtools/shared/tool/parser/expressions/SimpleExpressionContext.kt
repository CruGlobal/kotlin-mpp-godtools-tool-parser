package org.cru.godtools.shared.tool.parser.expressions

import androidx.annotation.RestrictTo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class SimpleExpressionContext(
    private val vars: MutableMap<String, List<String>?> = mutableMapOf()
) : ExpressionContext {
    private val varsChangeFlow = MutableSharedFlow<String>(extraBufferCapacity = Int.MAX_VALUE)

    override fun <T> varsChangeFlow(keys: Collection<String>?, block: (ExpressionContext) -> T) = when {
        keys.isNullOrEmpty() -> flowOf(Unit)
        else -> varsChangeFlow.onSubscription { emit(keys.first()) }.filter { it in keys }.map {}.conflate()
    }.map { block(this) }

    override fun getVar(key: String) = vars[key].orEmpty()

    override fun setVar(key: String, values: List<String>?) {
        vars[key] = values?.toList()
        varsChangeFlow.tryEmit(key)
    }
}
