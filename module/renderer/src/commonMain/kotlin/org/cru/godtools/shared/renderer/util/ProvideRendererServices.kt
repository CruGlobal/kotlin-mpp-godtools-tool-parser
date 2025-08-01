package org.cru.godtools.shared.renderer.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import okio.FileSystem
import org.cru.godtools.shared.renderer.tips.LocalTipsRepository
import org.cru.godtools.shared.renderer.tips.TipsRepository

@Composable
fun ProvideRendererServices(
    resources: FileSystem,
    tipsRepository: TipsRepository = LocalTipsRepository.current,
    content: @Composable () -> Unit
) = CompositionLocalProvider(
    LocalResourceFileSystem provides resources,
    LocalTipsRepository provides tipsRepository,
    content = content,
)
