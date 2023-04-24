@file:JvmMultifileClass
@file:JvmName("ImageKt")

package org.cru.godtools.shared.tool.parser.model

val Image?.gravity get() = this?.gravity ?: Image.DEFAULT_GRAVITY
val Image?.width get() = this?.width ?: Image.DEFAULT_WIDTH
