package org.cru.godtools.shared.tool.parser.model

import kotlinx.coroutines.flow.distinctUntilChanged
import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.ccci.gto.support.androidx.annotation.VisibleForTesting
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_MULTISELECT
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.model.Multiselect.Option.Style.Companion.toMultiselectOptionStyleOrNull
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren
import org.cru.godtools.shared.tool.state.State

private const val XML_STATE = "state"
private const val XML_COLUMNS = "columns"
private const val XML_SELECTION_LIMIT = "selection-limit"
private const val XML_OPTION = "option"
private const val XML_OPTION_STYLE = "option-style"

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

    private val optionStyle: Option.Style
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

        optionStyle =
            parser.getAttributeValue(XML_OPTION_STYLE).toMultiselectOptionStyleOrNull() ?: Option.DEFAULT_STYLE
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
        optionStyle: Option.Style = Option.DEFAULT_STYLE,
        optionBackgroundColor: PlatformColor? = null,
        optionSelectedColor: PlatformColor? = null,
        options: ((Multiselect) -> List<Option>)? = null
    ) : super(parent) {
        this.stateName = stateName
        columns = 1
        this.selectionLimit = selectionLimit
        this.optionStyle = optionStyle
        _optionBackgroundColor = optionBackgroundColor
        _optionSelectedColor = optionSelectedColor
        this.options = options?.invoke(this).orEmpty()
    }

    override val isIgnored get() = !manifest.config.supportsFeature(FEATURE_MULTISELECT) || super.isIgnored

    class Option : Content, Parent, HasAnalyticsEvents {
        internal companion object {
            private const val XML_SELECTED_COLOR = "selected-color"

            private const val XML_VALUE = "value"
            private const val XML_STYLE = "style"
            private const val XML_STYLE_CARD = "card"
            private const val XML_STYLE_FLAT = "flat"

            internal val DEFAULT_STYLE = Style.CARD
        }

        val multiselect: Multiselect

        private val _style: Style?
        val style get() = _style ?: multiselect.optionStyle

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

            _style = parser.getAttributeValue(XML_STYLE).toMultiselectOptionStyleOrNull()

            _backgroundColor = parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull()
            _selectedColor = parser.getAttributeValue(XML_SELECTED_COLOR)?.toColorOrNull()

            value = parser.getAttributeValue(XML_VALUE).orEmpty()

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
            style: Style? = null,
            analyticsEvents: List<AnalyticsEvent> = emptyList(),
            backgroundColor: PlatformColor? = null,
            selectedColor: PlatformColor? = null,
            value: String = ""
        ) : super(multiselect) {
            this.multiselect = multiselect
            _style = style
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
        fun isSelectedFlow(state: State) =
            state.varsChangeFlow(multiselect.stateName) { isSelected(it) }.distinctUntilChanged()
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

        enum class Style {
            CARD, FLAT;

            internal companion object {
                internal fun String?.toMultiselectOptionStyleOrNull() = when (this) {
                    XML_STYLE_CARD -> CARD
                    XML_STYLE_FLAT -> FLAT
                    else -> null
                }
            }
        }
    }
}

val Multiselect.Option?.backgroundColor get() = this?.backgroundColor ?: stylesParent.multiselectOptionBackgroundColor
val Multiselect.Option?.selectedColor get() = this?.selectedColor ?: stylesParent.defaultSelectedColor

@VisibleForTesting
internal val Styles?.defaultSelectedColor
    get() = primaryColor.toHSL().run { copy(alpha = 1f, l = (l + 0.4f).coerceAtMost(1f)) }.toPlatformColor()
