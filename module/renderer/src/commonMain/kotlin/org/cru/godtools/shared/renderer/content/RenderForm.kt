package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Form

@Composable
fun ColumnScope.RenderForm(model: Form, state: State) = RenderContent(model.content, state)
