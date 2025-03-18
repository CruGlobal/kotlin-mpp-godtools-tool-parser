@file:JvmMultifileClass
@file:JvmName("FlowKt")

package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.model.Flow.Companion.DEFAULT_ITEM_WIDTH
import org.cru.godtools.shared.tool.parser.model.Flow.Companion.DEFAULT_ROW_GRAVITY

val Flow?.rowGravity get() = this?.rowGravity ?: DEFAULT_ROW_GRAVITY
val Flow.Item?.width get() = this?.width ?: DEFAULT_ITEM_WIDTH
