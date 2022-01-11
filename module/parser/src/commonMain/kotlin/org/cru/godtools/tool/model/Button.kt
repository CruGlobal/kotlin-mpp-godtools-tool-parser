package org.cru.godtools.tool.model

import io.github.aakira.napier.Napier
import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.DeprecationException
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.model.AnalyticsEvent.Companion.parseAnalyticsEvents
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger
import org.cru.godtools.tool.model.Button.Style.Companion.toButtonStyle
import org.cru.godtools.tool.model.Button.Type.Companion.toButtonTypeOrNull
import org.cru.godtools.tool.model.Dimension.Companion.toDimensionOrNull
import org.cru.godtools.tool.model.Dimension.Pixels
import org.cru.godtools.tool.model.Gravity.Companion.toGravityOrNull
import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_COLOR = "color"
private const val XML_TYPE = "type"
private const val XML_TYPE_EVENT = "event"
private const val XML_TYPE_URL = "url"
private const val XML_STYLE = "style"
private const val XML_STYLE_CONTAINED = "contained"
private const val XML_STYLE_OUTLINED = "outlined"
private const val XML_GRAVITY = "gravity"
private const val XML_WIDTH = "width"
private const val XML_ICON = "icon"
private const val XML_ICON_GRAVITY = "icon-gravity"
private const val XML_ICON_SIZE = "icon-size"

private const val TAG = "Button"

class Button : Content, Styles, HasAnalyticsEvents, Clickable {
    internal companion object {
        internal const val XML_BUTTON = "button"

        internal val DEFAULT_GRAVITY = Gravity.Horizontal.CENTER
        internal val DEFAULT_WIDTH = Dimension.Percent(1f)
        internal val DEFAULT_BACKGROUND_COLOR = TRANSPARENT
        internal val DEFAULT_ICON_GRAVITY = Gravity.Horizontal.START
        internal const val DEFAULT_ICON_SIZE = 18
    }

    @Deprecated("Since v0.4.0, Button Type no longer controls any functionality.")
    val type get() = when {
        url != null -> Type.URL
        else -> Type.EVENT
    }
    override val events: List<EventId>
    override val url: Uri?

    private val _style: Style?
    val style: Style get() = _style ?: stylesParent.buttonStyle

    internal val gravity: Gravity.Horizontal
    internal val width: Dimension

    @AndroidColorInt
    private val _buttonColor: PlatformColor?
    @get:AndroidColorInt
    override val buttonColor get() = _buttonColor ?: stylesParent.let { it?.buttonColor ?: it.primaryColor }

    @AndroidColorInt
    val backgroundColor: PlatformColor

    private val iconName: String?
    val icon get() = getResource(iconName)
    internal val iconSize: Int
    internal val iconGravity: Gravity.Horizontal

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

        val type = parser.getAttributeValue(XML_TYPE).toButtonTypeOrNull()
        parser.parseClickableAttrs { events, url ->
            this.events = if (type == null || type == Type.EVENT) events else emptyList()
            this.url = if (type == null || type == Type.URL) url else null
        }

        _style = parser.getAttributeValue(XML_STYLE)?.toButtonStyle()
        gravity = parser.getAttributeValue(XML_GRAVITY).toGravityOrNull()?.horizontal ?: DEFAULT_GRAVITY
        width = parser.getAttributeValue(XML_WIDTH).toDimensionOrNull()?.takeIf { it is Pixels } ?: DEFAULT_WIDTH
        _buttonColor = parser.getAttributeValue(XML_COLOR)?.toColorOrNull()
        backgroundColor = parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull() ?: DEFAULT_BACKGROUND_COLOR

        iconName = parser.getAttributeValue(XML_ICON)
        iconGravity = parser.getAttributeValue(XML_ICON_GRAVITY).toGravityOrNull()?.horizontal ?: DEFAULT_ICON_GRAVITY
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
                Napier.e(message, DeprecationException("XML Analytics Event Deprecated trigger $message"), TAG)
            }
        }
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(
        parent: Base = Manifest(),
        style: Style? = null,
        @AndroidColorInt color: PlatformColor? = null,
        analyticsEvents: List<AnalyticsEvent> = emptyList(),
        events: List<EventId> = emptyList(),
        url: Uri? = null,
        text: ((Button) -> Text?)? = null
    ) : super(parent) {
        this.events = events
        this.url = url

        _style = style
        gravity = DEFAULT_GRAVITY
        width = DEFAULT_WIDTH
        _buttonColor = color
        backgroundColor = DEFAULT_BACKGROUND_COLOR

        iconName = null
        iconGravity = DEFAULT_ICON_GRAVITY
        iconSize = DEFAULT_ICON_SIZE

        this.analyticsEvents = analyticsEvents
        this.text = text?.invoke(this)
    }

    override val isIgnored get() = super.isIgnored || !isClickable || style == Style.UNKNOWN

    override fun getAnalyticsEvents(type: Trigger) = when (type) {
        Trigger.CLICKED ->
            analyticsEvents.filter { it.isTriggerType(Trigger.CLICKED, Trigger.SELECTED, Trigger.DEFAULT) }
        else -> error("The $type trigger type is currently unsupported on Buttons")
    }

    enum class Type {
        EVENT, URL, UNKNOWN;

        internal companion object {
            val DEFAULT = UNKNOWN

            fun String?.toButtonTypeOrNull() = when (this) {
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

val Button?.gravity get() = this?.gravity ?: Button.DEFAULT_GRAVITY
val Button?.width get() = this?.width ?: Button.DEFAULT_WIDTH

val Button?.buttonColor get() = this?.buttonColor ?: stylesParent.primaryColor
val Button?.textColor get() = this?.textColor ?: stylesParent.primaryTextColor
val Button?.iconSize get() = this?.iconSize ?: Button.DEFAULT_ICON_SIZE
val Button?.iconGravity get() = this?.iconGravity ?: Button.DEFAULT_ICON_GRAVITY
