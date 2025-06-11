@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import okio.Path.Companion.toPath
import org.cru.godtools.shared.tool.parser.model.Resource

internal fun Resource.toPath() = localName?.toPath()
