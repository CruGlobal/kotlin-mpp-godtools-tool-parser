@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.extensions

import androidx.compose.runtime.Composable
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import okio.Path.Companion.toPath
import org.cru.godtools.shared.renderer.util.LocalResourceFileSystem
import org.cru.godtools.shared.tool.parser.model.Resource

internal fun Resource.toPath() = localName?.toPath()

@Composable
internal fun Resource.toImageRequestBuilder() = ImageRequest.Builder(LocalPlatformContext.current)
    .fileSystem(LocalResourceFileSystem.current)
    .data(toPath())

@Composable
internal fun Resource.toImageRequest() = toImageRequestBuilder().build()
