@file:JvmMultifileClass
@file:JvmName("MultiselectKt")

package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import com.github.ajalt.colormath.Color
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.native.HiddenFromObjC
import kotlinx.coroutines.flow.distinctUntilChanged
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_MULTISELECT
import org.cru.godtools.shared.tool.parser.internal.toColorOrNull
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.model.Multiselect.Option.Style.Companion.toMultiselectOptionStyleOrNull
import org.cru.godtools.shared.tool.parser.util.FlowWatcher.Companion.watch
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_STATE = "state"
private const val XML_COLUMNS = "columns"
private const val XML_SELECTION_LIMIT = "selection-limit"
private const val XML_OPTION = "option"
private const val XML_OPTION_STYLE = "option-style"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
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
    @Suppress("ktlint:standard:property-naming") // https://github.com/pinterest/ktlint/issues/2448
    private val _optionBackgroundColor: Color?
    private val optionBackgroundColor get() = _optionBackgroundColor ?: stylesParent.multiselectOptionBackgroundColor
    @Suppress("ktlint:standard:property-naming") // https://github.com/pinterest/ktlint/issues/2448
    private val _optionSelectedColor: Color?
    private val optionSelectedColor get() = _optionSelectedColor ?: stylesParent?.multiselectOptionSelectedColor

    @JsExport.Ignore
    @JsName("_options")
    val options: List<Option>
    override val tips get() = options.flatMap { it.contentTips }

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

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        parent: Base = Manifest(),
        stateName: String = "",
        selectionLimit: Int = 1,
        optionStyle: Option.Style = Option.DEFAULT_STYLE,
        optionBackgroundColor: Color? = null,
        optionSelectedColor: Color? = null,
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

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("options")
    val jsOptions get() = options.toTypedArray()
    // endregion Kotlin/JS interop

    class Option : BaseModel, Parent, HasAnalyticsEvents {
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

        private val _backgroundColor: Color?
        @JsName("_backgroundColor")
        @JsExport.Ignore
        val backgroundColor get() = _backgroundColor ?: multiselect.optionBackgroundColor
        private val _selectedColor: Color?
        @JsName("_selectedColor")
        @JsExport.Ignore
        val selectedColor get() = _selectedColor ?: multiselect.optionSelectedColor ?: stylesParent.defaultSelectedColor

        @VisibleForTesting
        internal val value: String

        @VisibleForTesting
        internal val analyticsEvents: List<AnalyticsEvent>
        override val content: List<Content>

        internal constructor(multiselect: Multiselect, parser: XmlPullParser) : super(multiselect) {
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
                        AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents()
                    }
                }
            }
        }

        @RestrictTo(RestrictTo.Scope.TESTS)
        internal constructor(
            multiselect: Multiselect = Multiselect(),
            style: Style? = null,
            analyticsEvents: List<AnalyticsEvent> = emptyList(),
            backgroundColor: Color? = null,
            selectedColor: Color? = null,
            value: String = "",
            content: (Option) -> List<Content> = { emptyList() },
        ) : super(multiselect) {
            this.multiselect = multiselect
            _style = style
            _backgroundColor = backgroundColor
            _selectedColor = selectedColor
            this.value = value
            this.analyticsEvents = analyticsEvents
            this.content = content(this)
        }

        override fun getAnalyticsEvents(type: Trigger) = when (type) {
            Trigger.CLICKED -> analyticsEvents.filter { it.isTriggerType(Trigger.CLICKED, Trigger.DEFAULT) }
            else -> error("The $type trigger type is currently unsupported on Multiselect Options")
        }

        fun isSelected(state: State) = value in state.getVar(multiselect.stateName)
        fun isSelectedFlow(state: State) =
            state.varsChangeFlow(setOf(multiselect.stateName)) { isSelected(it) }.distinctUntilChanged()
        fun watchIsSelected(state: State, block: (isSelected: Boolean) -> Unit) = isSelectedFlow(state).watch(block)
        fun toggleSelected(state: State): Boolean {
            val current = state.getVar(multiselect.stateName)
            when {
                value in current -> state.removeVarValue(multiselect.stateName, value)
                current.size < multiselect.selectionLimit -> state.addVarValue(multiselect.stateName, value)
                multiselect.selectionLimit == 1 -> state.setVar(multiselect.stateName, listOf(value))
                else -> return false
            }
            return true
        }

        // region Kotlin/JS interop
        @HiddenFromObjC
        @JsName("backgroundColor")
        val platformBackgroundColor get() = backgroundColor.toPlatformColor()
        @HiddenFromObjC
        @JsName("selectedColor")
        val platformSelectedColor get() = selectedColor.toPlatformColor()
        // endregion Kotlin/JS interop

        enum class Style {
            CARD,
            FLAT;

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

internal val Styles?.defaultSelectedColor
    get() = primaryColor.toHSL().run { copy(alpha = 1f, l = (l + 0.4f).coerceAtMost(1f)) }
