@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.InternalCompottieApi
import io.github.alexzhirkevich.compottie.LottieAnimationFormat
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.decodeToLottieComposition
import io.github.alexzhirkevich.compottie.ioDispatcher
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.buffer
import org.cru.godtools.shared.tool.parser.model.Resource

@Suppress("FunctionName")
@OptIn(InternalCompottieApi::class)
internal fun LottieCompositionSpec.Companion.Resource(fileSystem: FileSystem, resource: Resource) =
    object : LottieCompositionSpec {
        override val key get() = "resource_${resource.localName}"

        override suspend fun load() = withContext(Compottie.ioDispatcher()) {
            fileSystem.source(resource.toPath()!!).buffer().readByteArray()
                .decodeToLottieComposition(LottieAnimationFormat.Undefined)
        }
    }
