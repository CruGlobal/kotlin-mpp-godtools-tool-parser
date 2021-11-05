package org.cru.godtools.tool.model

import org.cru.godtools.tool.FEATURE_MULTISELECT
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger
import org.cru.godtools.tool.state.State
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_STATE = "state"
private const val XML_COLUMNS = "columns"
private const val XML_SELECTION_LIMIT = "selection-limit"
private const val XML_OPTION = "option"
private const val XML_OPTION_VALUE = "value"

class Multiselect : Content {
    internal companion object {
        internal const val XML_MULTISELECT = "multiselect"

        internal const val XML_MULTISELECT_OPTION_BACKGROUND_COLOR = "multiselect-option-background-color"
        internal const val XML_MULTISELECT_OPTION_SELECTED_COLOR = "multiselect-option-selected-color"
        private const val XML_OPTION_BACKGROUND_COLOR = "option-background-color"
        private const val XML_OPTION_SELECTED_COLOR = "option-selected-color"
    }

    @VisibleForTesting
    internal val stateName: String

    val columns: Int
    @VisibleForTesting
    internal val selectionLimit: Int

    private val _optionBackgroundColor: PlatformColor?
    private val optionBackgroundColor get() = _optionBackgroundColor ?: stylesParent.multiselectOptionBackgroundColor
    private val _optionSelectedColor: PlatformColor?
    private val optionSelectedColor get() = _optionSelectedColor ?: stylesParent?.multiselectOptionSelectedColor

    val options: List<Option>

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_MULTISELECT)

        stateName = parser.getAttributeValue(XML_STATE).orEmpty()

        columns = parser.getAttributeValue(XML_COLUMNS)?.toIntOrNull() ?: 1
        selectionLimit = (parser.getAttributeValue(XML_SELECTION_LIMIT)?.toIntOrNull() ?: 1).coerceAtLeast(1)

        _optionBackgroundColor = parser.getAttributeValue(XML_OPTION_BACKGROUND_COLOR)?.toColorOrNull()
        _optionSelectedColor = parser.getAttributeValue(XML_OPTION_SELECTED_COLOR)?.toColorOrNull()

        options = mutableListOf()
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_CONTENT -> when (parser.name) {
                    XML_OPTION -> options += Option(this, parser)
                }
            }
        }
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(
        parent: Base = Manifest(),
        stateName: String = "",
        selectionLimit: Int = 1,
        optionBackgroundColor: PlatformColor? = null,
        optionSelectedColor: PlatformColor? = null,
        options: ((Multiselect) -> List<Option>)? = null
    ) : super(parent) {
        this.stateName = stateName
        columns = 1
        this.selectionLimit = selectionLimit
        _optionBackgroundColor = optionBackgroundColor
        _optionSelectedColor = optionSelectedColor
        this.options = options?.invoke(this).orEmpty()
    }

    override val isIgnored get() = FEATURE_MULTISELECT !in ParserConfig.supportedFeatures || super.isIgnored

    class Option : Content, Parent, HasAnalyticsEvents {
        private companion object {
            private const val XML_SELECTED_COLOR = "selected-color"
        }

        val multiselect: Multiselect

        private val _backgroundColor: PlatformColor?
        internal val backgroundColor get() = _backgroundColor ?: multiselect.optionBackgroundColor
        private val _selectedColor: PlatformColor?
        internal val selectedColor
            get() = _selectedColor ?: multiselect.optionSelectedColor ?: stylesParent.defaultSelectedColor

        @VisibleForTesting
        internal val value: String

        @VisibleForTesting
        internal val analyticsEvents: List<AnalyticsEvent>
        override val content: List<Content>

        internal constructor(multiselect: Multiselect, parser: XmlPullParser) : super(multiselect, parser) {
            this.multiselect = multiselect
            parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_OPTION)

            _backgroundColor = parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull()
            _selectedColor = parser.getAttributeValue(XML_SELECTED_COLOR)?.toColorOrNull()

            value = parser.getAttributeValue(XML_OPTION_VALUE).orEmpty()

            analyticsEvents = mutableListOf()
            content = parseContent(parser) {
                when (parser.namespace) {
                    XMLNS_ANALYTICS -> when (parser.name) {
                        AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                    }
                }
            }
        }

        @RestrictTo(RestrictToScope.TESTS)
        internal constructor(
            multiselect: Multiselect = Multiselect(),
            analyticsEvents: List<AnalyticsEvent> = emptyList(),
            backgroundColor: PlatformColor? = null,
            selectedColor: PlatformColor? = null,
            value: String = ""
        ) : super(multiselect) {
            this.multiselect = multiselect
            _backgroundColor = backgroundColor
            _selectedColor = selectedColor
            this.value = value
            this.analyticsEvents = analyticsEvents
            content = emptyList()
        }

        override fun getAnalyticsEvents(type: Trigger) = when (type) {
            Trigger.CLICKED -> analyticsEvents.filter { it.isTriggerType(Trigger.CLICKED, Trigger.DEFAULT) }
            else -> error("The $type trigger type is currently unsupported on Multiselect Options")
        }

        fun isSelected(state: State) = value in state.getAll(multiselect.stateName)
        fun isSelectedFlow(state: State) = state.changeFlow(multiselect.stateName) { isSelected(it) }
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

val Multiselect.Option?.backgroundColor get() = this?.backgroundColor ?: stylesParent.multiselectOptionBackgroundColor
val Multiselect.Option?.selectedColor get() = this?.selectedColor ?: stylesParent.defaultSelectedColor

@VisibleForTesting
internal val Styles?.defaultSelectedColor
    get() = primaryColor.toHSL().run { copy(alpha = 1f, l = (l + 0.4f).coerceAtMost(1f)) }.toPlatformColor()
