@file:Suppress("ktlint:compose:compositionlocal-allowlist")

package org.cru.godtools.shared.renderer.tips

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import io.fluidsonic.locale.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.cru.godtools.shared.tool.parser.model.tips.Tip

interface TipsRepository {
    suspend fun markTipComplete(tool: String, locale: Locale, tipId: String)
    fun isTipCompleteFlow(tool: String, locale: Locale, tipId: String): Flow<Boolean>
}

internal val LocalTipsRepository = staticCompositionLocalOf<TipsRepository> { InMemoryTipsRepository() }

@Composable
internal fun Tip.produceIsComplete(): State<Boolean> {
    val tipsRepository = LocalTipsRepository.current
    val tool = manifest.code
    val locale = manifest.locale
    val tipId = id
    return remember(tipsRepository, tool, locale, tipId) {
        when {
            tool == null || locale == null -> flowOf(false)
            else -> tipsRepository.isTipCompleteFlow(tool, locale, tipId)
        }
    }.collectAsState(false)
}
