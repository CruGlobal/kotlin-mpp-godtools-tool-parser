package org.cru.godtools.shared.tool.parser.model

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import org.cru.godtools.shared.renderer.state.State
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

    fun isInvisible(state: State) = invisibleIf?.evaluate(state) == true
    fun isInvisibleFlow(state: State) =
        state.varsChangeFlow(invisibleIf?.vars()) { isInvisible(it) }.distinctUntilChanged()
    fun isGone(state: State) = goneIf?.evaluate(state) == true
    fun isGoneFlow(state: State) = state.varsChangeFlow(goneIf?.vars()) { isGone(it) }.distinctUntilChanged()
    fun getVisibility(state: State): VisibilityEnum {
        if (isInvisible(state = state)) {
            return VisibilityEnum.INVISIBLE
        } else if (isGone(state = state)) {
            return VisibilityEnum.GONE
        }
        return VisibilityEnum.VISIBLE
    }

    fun watchIsGone(state: State, block: (Boolean) -> Unit) = isGoneFlow(state).watch(block)
    fun watchIsInvisible(state: State, block: (Boolean) -> Unit) = isInvisibleFlow(state).watch(block)
    fun watchVisibility(state: State, block: (isInvisible: Boolean, isGone: Boolean) -> Unit) =
        isInvisibleFlow(state).combine(isGoneFlow(state)) { invisible, gone -> Pair(invisible, gone) }
            .watch { block(it.first, it.second) }
}

enum class VisibilityEnum {
    GONE,
    INVISIBLE,
    VISIBLE
}

@OptIn(ExperimentalContracts::class)
internal inline fun XmlPullParser.parseVisibilityAttrs(block: (invisibleIf: Expression?, goneIf: Expression?) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block(getAttributeValue(XML_INVISIBLE_IF).toExpressionOrNull(), getAttributeValue(XML_GONE_IF).toExpressionOrNull())
}
