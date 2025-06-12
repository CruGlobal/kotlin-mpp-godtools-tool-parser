package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.window.ComposeUIViewController
import okio.FileSystem
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.ProvideRendererServices
import org.cru.godtools.shared.tool.parser.model.Content
import platform.UIKit.UIColor

@Suppress("FunctionName")
fun ContentStackView(content: List<Content>, state: State, fileSystem: FileSystem) = ComposeUIViewController {
    ProvideRendererServices(resources = fileSystem) {
        RenderContentStack(content, state = state)
    }
}.apply {
    view.backgroundColor = UIColor(white = 0.0, alpha = 0.0)
}
