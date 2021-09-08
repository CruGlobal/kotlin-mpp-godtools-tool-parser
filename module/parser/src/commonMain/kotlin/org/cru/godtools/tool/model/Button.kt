package org.cru.godtools.tool.model

import io.github.aakira.napier.Napier
import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger
import org.cru.godtools.tool.model.Button.Style.Companion.toButtonStyle
import org.cru.godtools.tool.model.Button.Type.Companion.toButtonTypeOrNull
import org.cru.godtools.tool.model.ImageGravity.Companion.toImageGravityOrNull
import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_COLOR = "color"
private const val XML_TYPE = "type"
private const val XML_TYPE_EVENT = "event"
private const val XML_TYPE_URL = "url"
private const val XML_STYLE = "style"
private const val XML_STYLE_CONTAINED = "contained"
private const val XML_STYLE_OUTLINED = "outlined"
private const val XML_URL = "url"
private const val XML_ICON = "icon"
private const val XML_ICON_GRAVITY = "icon-gravity"
private const val XML_ICON_SIZE = "icon-size"

private const val TAG = "Button"

class Button : Content, Styles, HasAnalyticsEvents {
    internal companion object {
        internal const val XML_BUTTON = "button"

        internal val DEFAULT_BACKGROUND_COLOR = TRANSPARENT
        internal val DEFAULT_ICON_GRAVITY = ImageGravity.START
        internal const val DEFAULT_ICON_SIZE = 18
    }

    val type: Type
    val events: List<EventId>
    val url: Uri?

    private val _style: Style?
    val style: Style get() = _style ?: stylesParent.buttonStyle

    @AndroidColorInt
    private val _buttonColor: PlatformColor?
    @get:AndroidColorInt
    override val buttonColor get() = _buttonColor ?: stylesParent.let { it?.buttonColor ?: it.primaryColor }

    @AndroidColorInt
    val backgroundColor: PlatformColor

    private val iconName: String?
    val icon get() = getResource(iconName)
    internal val iconSize: Int
    internal val iconGravity: ImageGravity

    val text: Text?
    override val textAlign get() = Text.Align.CENTER
    override val textColor
        get() = when (style) {
            Style.CONTAINED, Style.UNKNOWN -> primaryTextColor
            Style.OUTLINED -> buttonColor
        }

    val analyticsEvents: List<AnalyticsEvent>

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_BUTTON)

        type = parser.getAttributeValue(XML_TYPE)?.toButtonTypeOrNull() ?: Type.DEFAULT
        events = parser.getAttributeValue(XML_EVENTS).toEventIds()
        url = parser.getAttributeValue(XML_URL)?.toAbsoluteUri()

        _style = parser.getAttributeValue(XML_STYLE)?.toButtonStyle()
        _buttonColor = parser.getAttributeValue(XML_COLOR)?.toColorOrNull()
        backgroundColor = parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull() ?: DEFAULT_BACKGROUND_COLOR

        iconName = parser.getAttributeValue(XML_ICON)
        iconGravity = parser.getAttributeValue(XML_ICON_GRAVITY)?.toImageGravityOrNull() ?: DEFAULT_ICON_GRAVITY
        iconSize = parser.getAttributeValue(XML_ICON_SIZE)?.toIntOrNull() ?: DEFAULT_ICON_SIZE

        // process any child elements
        analyticsEvents = mutableListOf()
        text = parser.parseTextChild(this, XMLNS_CONTENT, XML_BUTTON) {
            when (parser.namespace) {
                XMLNS_ANALYTICS -> when (parser.name) {
                    AnalyticsEvent.XML_EVENTS -> analyticsEvents += parser.parseAnalyticsEvents(this)
                }
            }
        }

        // Log a non-fatal warning if any analytics event is still using the SELECTED trigger
        analyticsEvents.forEach {
            if (it.trigger == Trigger.SELECTED) {
                val message =
                    "tool: ${manifest.code} locale: ${manifest.locale} action: ${it.action} trigger: ${it.trigger}"
                Napier.e(message, UnsupportedOperationException("XML Analytics Event Deprecated trigger $message"), TAG)
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        parent: Base = Manifest(),
        type: Type = Type.DEFAULT,
        style: Style? = null,
        @AndroidColorInt color: PlatformColor? = null,
        analyticsEvents: List<AnalyticsEvent> = emptyList(),
        text: ((Button) -> Text?)? = null
    ) : super(parent) {
        this.type = type
        events = emptyList()
        url = null

        _style = style
        _buttonColor = color
        backgroundColor = DEFAULT_BACKGROUND_COLOR

        iconName = null
        iconGravity = DEFAULT_ICON_GRAVITY
        iconSize = DEFAULT_ICON_SIZE

        this.analyticsEvents = analyticsEvents
        this.text = text?.invoke(this)
    }

    override val isIgnored get() = super.isIgnored || type == Type.UNKNOWN || style == Style.UNKNOWN

    override fun getAnalyticsEvents(type: Trigger) = when (type) {
        Trigger.CLICKED ->
            analyticsEvents.filter { it.isTriggerType(Trigger.CLICKED, Trigger.SELECTED, Trigger.DEFAULT) }
        else -> error("The $type trigger type is currently unsupported on Buttons")
    }

    enum class Type {
        EVENT, URL, UNKNOWN;

        internal companion object {
            val DEFAULT = UNKNOWN

            fun String.toButtonTypeOrNull() = when (this) {
                XML_TYPE_EVENT -> EVENT
                XML_TYPE_URL -> URL
                else -> null
            }
        }
    }

    enum class Style {
        CONTAINED, OUTLINED, UNKNOWN;

        internal companion object {
            fun String.toButtonStyle() = when (this) {
                XML_STYLE_CONTAINED -> CONTAINED
                XML_STYLE_OUTLINED -> OUTLINED
                else -> UNKNOWN
            }
        }
    }
}

val Button?.buttonColor get() = this?.buttonColor ?: stylesParent.primaryColor
val Button?.textColor get() = this?.textColor ?: stylesParent.primaryTextColor
val Button?.iconSize get() = this?.iconSize ?: Button.DEFAULT_ICON_SIZE
val Button?.iconGravity get() = this?.iconGravity ?: Button.DEFAULT_ICON_GRAVITY
