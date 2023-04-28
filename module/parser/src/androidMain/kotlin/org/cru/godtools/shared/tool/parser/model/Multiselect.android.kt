@file:JvmMultifileClass
@file:JvmName("MultiselectKt")

package org.cru.godtools.shared.tool.parser.model

val Multiselect.Option?.backgroundColor get() = this?.backgroundColor ?: stylesParent.multiselectOptionBackgroundColor
val Multiselect.Option?.selectedColor get() = this?.selectedColor ?: stylesParent.defaultSelectedColor
