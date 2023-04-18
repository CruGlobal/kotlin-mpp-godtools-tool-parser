package org.cru.godtools.shared.tool.parser.model

import kotlinx.coroutines.flow.distinctUntilChanged
import org.cru.godtools.shared.tool.parser.expressions.Expression
import org.cru.godtools.shared.tool.parser.expressions.toExpressionOrNull
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.state.State
import org.cru.godtools.shared.tool.state.varsChangeFlow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private const val XML_INVISIBLE_IF = "invisible-if"
private const val XML_GONE_IF = "gone-if"

interface Visibility {
    val invisibleIf: Expression?
    val goneIf: Expression?

    fun isInvisible(state: State) = invisibleIf?.evaluate(state) ?: false
    fun isInvisibleFlow(state: State) =
        state.varsChangeFlow(invisibleIf?.vars()) { isInvisible(it) }.distinctUntilChanged()
    fun isGone(state: State) = goneIf?.evaluate(state) ?: false
    fun isGoneFlow(state: State) = state.varsChangeFlow(goneIf?.vars()) { isGone(it) }.distinctUntilChanged()
}

@OptIn(ExperimentalContracts::class)
internal inline fun XmlPullParser.parseVisibilityAttrs(block: (invisibleIf: Expression?, goneIf: Expression?) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block(getAttributeValue(XML_INVISIBLE_IF).toExpressionOrNull(), getAttributeValue(XML_GONE_IF).toExpressionOrNull())
}
