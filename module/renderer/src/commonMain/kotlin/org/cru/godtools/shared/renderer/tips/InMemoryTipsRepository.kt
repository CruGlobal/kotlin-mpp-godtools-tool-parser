package org.cru.godtools.shared.renderer.tips

import io.fluidsonic.locale.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription

class InMemoryTipsRepository : TipsRepository {
    private data class Key(val tool: String, val locale: Locale, val tipId: String)

    private val completedTips = mutableSetOf<Key>()
    private val changeFlow = MutableSharedFlow<Key>(extraBufferCapacity = 10)

    override suspend fun markTipComplete(tool: String, locale: Locale, tipId: String) {
        val key = Key(tool, locale, tipId)
        completedTips.add(key)
        changeFlow.emit(key)
    }

    override fun isTipCompleteFlow(tool: String, locale: Locale, tipId: String): Flow<Boolean> {
        val key = Key(tool, locale, tipId)
        return changeFlow
            .onSubscription { emit(key) }
            .filter { it == key }
            .map { key in completedTips }
    }
}
