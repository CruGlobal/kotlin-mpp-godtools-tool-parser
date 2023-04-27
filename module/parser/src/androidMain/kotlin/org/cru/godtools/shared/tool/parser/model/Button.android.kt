@file:JvmMultifileClass
@file:JvmName("ButtonKt")

package org.cru.godtools.shared.tool.parser.model

val Button?.gravity get() = this?.gravity ?: Button.DEFAULT_GRAVITY
val Button?.width get() = this?.width ?: Button.DEFAULT_WIDTH
