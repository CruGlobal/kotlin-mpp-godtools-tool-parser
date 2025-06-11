package org.cru.godtools.shared.renderer.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import okio.FileSystem

@Composable
fun ProvideRendererServices(resources: FileSystem, content: @Composable () -> Unit) = CompositionLocalProvider(
    LocalResourceFileSystem provides resources,
    content = content,
)
