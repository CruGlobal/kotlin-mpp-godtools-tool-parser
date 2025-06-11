package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.window.ComposeUIViewController
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Content
import platform.UIKit.UIViewController

@Suppress("ktlint:standard:function-naming")
fun ContentStackView(content: List<Content>, state: State): UIViewController = ComposeUIViewController {
    RenderContentStack(content, state = state)
}
