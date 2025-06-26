package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Input
import org.cru.godtools.shared.tool.parser.model.primaryColor
import org.cru.godtools.shared.tool.parser.model.stylesParent

internal const val TEST_TAG_INPUT = "input"

@Composable
internal fun RenderInput(model: Input, state: State) {
    if (model.type == Input.Type.HIDDEN) return

    OutlinedTextField(
        value = remember(model.id) { state.formFieldValueFlow(model.id) }
            .collectAsState(state.formFieldValue(model.id)).value.orEmpty(),
        onValueChange = { state.updateFormFieldValue(model.id, it) },
        label = { model.label?.let { RenderTextNode(it) } },
        placeholder = model.placeholder?.let { { RenderTextNode(it) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = when (model.type) {
                Input.Type.TEXT -> KeyboardType.Text
                Input.Type.EMAIL -> KeyboardType.Email
                Input.Type.PHONE -> KeyboardType.Phone
                else -> KeyboardType.Unspecified
            },
        ),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = model.stylesParent.primaryColor.toComposeColor(),
            focusedBorderColor = model.stylesParent.primaryColor.toComposeColor(),
        ),
        modifier = Modifier
            .testTag(TEST_TAG_INPUT)
            .padding(horizontal = Horizontal_Padding)
            .fillMaxWidth()
    )
}
