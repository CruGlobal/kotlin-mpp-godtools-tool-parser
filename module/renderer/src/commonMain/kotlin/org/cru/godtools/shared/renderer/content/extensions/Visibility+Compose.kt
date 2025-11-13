@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.ccci.gto.android.common.compose.ui.draw.invisibleIf
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Visibility

@Composable
internal fun Modifier.visibility(model: Visibility, state: State) = invisibleIf(model.produceIsInvisible(state).value)

@Composable
internal fun Visibility.produceIsInvisible(state: State) = remember(this, state) { isInvisibleFlow(state) }
    .collectAsState(isInvisible(state))
