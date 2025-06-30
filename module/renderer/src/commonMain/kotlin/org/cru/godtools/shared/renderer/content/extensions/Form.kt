package org.cru.godtools.shared.renderer.content.extensions

import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Form
import org.cru.godtools.shared.tool.parser.model.Input
import org.cru.godtools.shared.tool.parser.model.descendants

internal fun Form.submitForm(state: State): Boolean {
    val fields = mutableMapOf<String, String>()

    // validate all inputs
    val inputs = descendants.filterIsInstance<Input>()
    inputs.forEach {
        val value = when (it.type) {
            Input.Type.HIDDEN -> it.value
            else -> state.formFieldValue(it.id)
        }
        if (it.validateValue(value) != null) {
            inputs.forEach { state.toggleFormFieldValidation(it.id, true) }
            return false
        }

        val name = it.name
        if (name != null && value != null) fields[name] = value
    }

    // trigger the SubmitForm event
    state.triggerEvent(State.Event.SubmitForm(fields = fields))
    return true
}
