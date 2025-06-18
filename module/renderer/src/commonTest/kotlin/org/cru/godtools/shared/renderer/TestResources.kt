package org.cru.godtools.shared.renderer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil3.ColorImage
import coil3.test.FakeImageLoaderEngine
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import org.ccci.gto.android.common.okio.chroot
import org.ccci.gto.android.common.okio.readOnly
import org.cru.godtools.shared.tool.parser.model.Resource

object TestResources {
    @OptIn(ExperimentalUuidApi::class)
    val fileSystem = FileSystem.SYSTEM
        .readOnly()
        .chroot(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / Uuid.random().toString())

    val coilEngine = FakeImageLoaderEngine.Builder()
        .intercept("black_square.png".toPath(), ColorImage(Color.Black.toArgb(), width = 10, height = 10))
        .intercept("red_square.png".toPath(), ColorImage(Color.Red.toArgb(), width = 10, height = 10))
        .intercept("green_square.png".toPath(), ColorImage(Color.Green.toArgb(), width = 10, height = 10))
        .intercept("tall.png".toPath(), ColorImage(Color.Black.toArgb(), width = 10, height = 60))
        .intercept("wide.png".toPath(), ColorImage(Color.Black.toArgb(), width = 60, height = 10))
        .build()

    val resources = listOf(
        Resource(name = "black_square", localName = "black_square.png"),
        Resource(name = "red_square", localName = "red_square.png"),
        Resource(name = "green_square", localName = "green_square.png"),
        Resource(name = "tall", localName = "tall.png"),
        Resource(name = "wide", localName = "wide.png")
    )
}
