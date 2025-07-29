@file:JvmMultifileClass
@file:JvmName("PageKt")

package org.cru.godtools.shared.tool.parser.model.page

import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

val Page?.backgroundColor get() = this?.backgroundColor ?: DEFAULT_BACKGROUND_COLOR
val Page?.backgroundImageGravity get() = this?.backgroundImageGravity ?: DEFAULT_BACKGROUND_IMAGE_GRAVITY
val Page?.backgroundImageScaleType get() = this?.backgroundImageScaleType ?: DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE

val Page?.controlColor get() = this?.controlColor ?: DEFAULT_CONTROL_COLOR
