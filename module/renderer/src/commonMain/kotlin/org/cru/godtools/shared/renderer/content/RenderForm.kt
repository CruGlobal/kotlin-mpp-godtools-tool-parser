package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Form

@Composable
internal fun ColumnScope.RenderForm(form: Form, state: State) = Column(modifier = Modifier.visibility(form, state)) {
    RenderContent(form.content, state)
}
