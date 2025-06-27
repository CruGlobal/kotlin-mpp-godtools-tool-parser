package org.cru.godtools.shared.renderer.content.extensions

import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Form
import org.cru.godtools.shared.tool.parser.model.Input
import org.cru.godtools.shared.tool.parser.model.descendants

internal fun Form.submitForm(state: State): Boolean {
    val inputs = descendants.filterIsInstance<Input>()

    // TODO: validate inputs

    // trigger the SubmitForm event
    state.triggerEvent(
        State.Event.SubmitForm(
            fields = buildMap {
                inputs.forEach {
                    val name = it.name
                    val value = when (it.type) {
                        Input.Type.HIDDEN -> it.value
                        else -> state.formFieldValue(it.id)
                    }
                    if (name != null && value != null) put(name, value)
                }
            },
        ),
    )

    return true
}
