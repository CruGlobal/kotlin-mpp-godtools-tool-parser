@file:Suppress("ktlint:compose:compositionlocal-allowlist")

package org.cru.godtools.shared.renderer.tips

import androidx.compose.runtime.staticCompositionLocalOf
import io.fluidsonic.locale.Locale
import kotlinx.coroutines.flow.Flow

interface TipsRepository {
    suspend fun markTipComplete(tool: String, locale: Locale, tipId: String)
    fun isTipCompleteFlow(tool: String, locale: Locale, tipId: String): Flow<Boolean>
}

internal val LocalTipsRepository = staticCompositionLocalOf<TipsRepository> { InMemoryTipsRepository() }
