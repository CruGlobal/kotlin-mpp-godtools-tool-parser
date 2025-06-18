package org.cru.godtools.shared.tool.parser.model

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import org.cru.godtools.shared.renderer.state.ExpressionContext
import org.cru.godtools.shared.tool.parser.expressions.Expression
import org.cru.godtools.shared.tool.parser.expressions.toExpressionOrNull
import org.cru.godtools.shared.tool.parser.util.FlowWatcher.Companion.watch
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser

private const val XML_INVISIBLE_IF = "invisible-if"
private const val XML_GONE_IF = "gone-if"

@JsExport
@OptIn(ExperimentalJsExport::class)
interface Visibility {
    val invisibleIf: Expression?
    val goneIf: Expression?

    fun isInvisible(ctx: ExpressionContext) = invisibleIf?.evaluate(ctx) == true
    fun isInvisibleFlow(ctx: ExpressionContext) =
        ctx.varsChangeFlow(invisibleIf?.vars()) { isInvisible(it) }.distinctUntilChanged()
    fun isGone(ctx: ExpressionContext) = goneIf?.evaluate(ctx) == true
    fun isGoneFlow(ctx: ExpressionContext) = ctx.varsChangeFlow(goneIf?.vars()) { isGone(it) }.distinctUntilChanged()

    fun watchIsGone(ctx: ExpressionContext, block: (Boolean) -> Unit) = isGoneFlow(ctx).watch(block)
    fun watchIsInvisible(ctx: ExpressionContext, block: (Boolean) -> Unit) = isInvisibleFlow(ctx).watch(block)
    fun watchVisibility(ctx: ExpressionContext, block: (isInvisible: Boolean, isGone: Boolean) -> Unit) =
        isInvisibleFlow(ctx).combine(isGoneFlow(ctx)) { invisible, gone -> Pair(invisible, gone) }
            .watch { block(it.first, it.second) }
}

@OptIn(ExperimentalContracts::class)
internal inline fun XmlPullParser.parseVisibilityAttrs(block: (invisibleIf: Expression?, goneIf: Expression?) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block(getAttributeValue(XML_INVISIBLE_IF).toExpressionOrNull(), getAttributeValue(XML_GONE_IF).toExpressionOrNull())
}
