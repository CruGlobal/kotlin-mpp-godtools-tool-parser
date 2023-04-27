@file:JvmMultifileClass
@file:JvmName("CardKt")

package org.cru.godtools.shared.tool.parser.model

val Card?.backgroundColor get() = this?.backgroundColor ?: stylesParent.cardBackgroundColor
