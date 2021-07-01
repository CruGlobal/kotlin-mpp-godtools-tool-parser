package org.cru.godtools.tool.model

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.cru.godtools.tool.FEATURE_MULTISELECT
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.state.State
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_STATE = "state"
private const val XML_SELECTION_LIMIT = "selection-limit"
private const val XML_OPTION = "option"
private const val XML_OPTION_VALUE = "value"

class Multiselect : Content {
    internal companion object {
        internal const val XML_MULTISELECT = "multiselect"
    }

    @VisibleForTesting
    internal val stateName: String
    @VisibleForTesting
    internal val selectionLimit: Int

    val options: List<Option>

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_MULTISELECT)

        stateName = parser.getAttributeValue(XML_STATE).orEmpty()
        selectionLimit = (parser.getAttributeValue(XML_SELECTION_LIMIT)?.toIntOrNull() ?: 1).coerceAtLeast(1)

        options = mutableListOf()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_CONTENT -> when (parser.name) {
                    XML_OPTION -> options += Option(this, parser)
                }
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        parent: Base = Manifest(),
        stateName: String = "",
        selectionLimit: Int = 1,
        options: ((Multiselect) -> List<Option>)? = null
    ) : super(parent) {
        this.stateName = stateName
        this.selectionLimit = selectionLimit
        this.options = options?.invoke(this).orEmpty()
    }

    override val isIgnored get() = FEATURE_MULTISELECT !in ParserConfig.supportedFeatures || super.isIgnored

    class Option : Content, Parent {
        private val multiselect: Multiselect

        @VisibleForTesting
        internal val value: String

        override val content: List<Content>

        internal constructor(multiselect: Multiselect, parser: XmlPullParser) : super(multiselect, parser) {
            this.multiselect = multiselect
            parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_OPTION)

            value = parser.getAttributeValue(XML_OPTION_VALUE).orEmpty()

            content = parseContent(parser)
        }

        @RestrictTo(RestrictTo.Scope.TESTS)
        internal constructor(multiselect: Multiselect, value: String = "") : super(multiselect) {
            this.multiselect = multiselect
            this.value = value
            content = emptyList()
        }

        fun isSelected(state: State) = value in state.getAll(multiselect.stateName)
        fun isSelectedFlow(state: State) = state.changeFlow(multiselect.stateName)
            .map { isSelected(state) }
            .distinctUntilChanged()
        fun toggleSelected(state: State): Boolean {
            val current = state.getAll(multiselect.stateName)
            when {
                value in current -> state.removeValue(multiselect.stateName, value)
                current.size < multiselect.selectionLimit -> state.addValue(multiselect.stateName, value)
                multiselect.selectionLimit == 1 -> state[multiselect.stateName] = value
                else -> return false
            }
            return true
        }
    }
}
