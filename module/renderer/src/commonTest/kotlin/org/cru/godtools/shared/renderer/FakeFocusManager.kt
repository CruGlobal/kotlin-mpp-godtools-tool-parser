package org.cru.godtools.shared.renderer

import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager

class FakeFocusManager : FocusManager {
    var clearFocusCalled = 0

    override fun clearFocus(force: Boolean) {
        clearFocusCalled++
    }
    override fun moveFocus(focusDirection: FocusDirection) = false
}
