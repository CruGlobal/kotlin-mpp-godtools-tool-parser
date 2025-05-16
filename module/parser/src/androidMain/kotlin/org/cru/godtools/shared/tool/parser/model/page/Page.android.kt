@file:JvmMultifileClass
@file:JvmName("PageKt")

package org.cru.godtools.shared.tool.parser.model.page

val Page?.controlColor get() = this?.controlColor ?: DEFAULT_CONTROL_COLOR
