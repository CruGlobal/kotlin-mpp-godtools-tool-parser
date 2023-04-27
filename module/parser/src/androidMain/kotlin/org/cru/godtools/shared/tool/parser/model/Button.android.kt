@file:JvmMultifileClass
@file:JvmName("ButtonKt")

package org.cru.godtools.shared.tool.parser.model

val Button?.gravity get() = this?.gravity ?: Button.DEFAULT_GRAVITY
val Button?.width get() = this?.width ?: Button.DEFAULT_WIDTH

val Button?.iconGravity get() = this?.iconGravity ?: Button.DEFAULT_ICON_GRAVITY
val Button?.iconSize get() = this?.iconSize ?: Button.DEFAULT_ICON_SIZE
