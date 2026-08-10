@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.util

import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Manifest

internal fun State.triggerScreenView(manifest: Manifest, screenName: String) = triggerEvent(
    State.Event.AnalyticsEvent.ScreenView(
        tool = manifest.code,
        locale = manifest.locale,
        screenName = screenName,
    )
)
