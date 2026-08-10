package org.cru.godtools.shared.renderer.tips

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.tips.Tip

private val TipSheetMaxHeight = 450.dp
private val TipSheetFullHeightThreshold = 500.dp

/**
 * Render a training [tip] within a modal bottom sheet.
 *
 * The sheet is capped at a max height of 450dp, unless there is less than 500dp of height available, in which case the
 * sheet expands to the full available height.
 *
 * @param onDismiss triggered when the user dismisses the sheet, either by swiping it away, tapping the scrim, using
 * the close button, or completing the last page of the tip. The caller is responsible for removing this composable
 * from the composition.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RenderTipBottomSheet(
    tip: Tip,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    state: State = remember { State() },
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        BoxWithConstraints {
            RenderTip(
                tip,
                state = state,
                onDismiss = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        when {
                            maxHeight < TipSheetFullHeightThreshold -> Modifier.fillMaxHeight()
                            else -> Modifier.heightIn(max = TipSheetMaxHeight)
                        }
                    )
            )
        }
    }
}
