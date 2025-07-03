package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.kotlin.renderer.generated.resources.Res
import org.cru.godtools.kotlin.renderer.generated.resources.tool_renderer_input_error_invalid_email
import org.cru.godtools.kotlin.renderer.generated.resources.tool_renderer_input_error_required
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Input
import org.cru.godtools.shared.tool.parser.model.primaryColor
import org.cru.godtools.shared.tool.parser.model.stylesParent
import org.jetbrains.compose.resources.stringResource

internal const val TEST_TAG_INPUT = "input"

@Composable
internal fun RenderInput(model: Input, state: State) {
    if (model.type == Input.Type.HIDDEN) return

    val id = model.id
    val value by remember(id) { state.formFieldValueFlow(id) }.collectAsState(state.formFieldValue(id))

    val validationEnabled by remember(id) { state.isFormFieldValidationEnabledFlow(id) }
        .collectAsState(state.isFormFieldValidationEnabled(id))
    val error by remember(model) { derivedStateOf { if (validationEnabled) model.validateValue(value) else null } }

    OutlinedTextField(
        value = value.orEmpty(),
        onValueChange = { state.updateFormFieldValue(id, it) },
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
        supportingText = error?.let { { Text(it.stringResource(model)) } },
        isError = error != null,
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

@Composable
private fun Input.Error.stringResource(input: Input) = when (this) {
    Input.Error.Required -> stringResource(Res.string.tool_renderer_input_error_required, input.name.orEmpty())
    Input.Error.InvalidEmail -> stringResource(Res.string.tool_renderer_input_error_invalid_email)
}
