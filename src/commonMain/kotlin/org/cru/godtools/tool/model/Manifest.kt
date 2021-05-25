package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.fluidlocale.PlatformLocale
import org.cru.godtools.tool.internal.fluidlocale.toLocaleOrNull
import org.cru.godtools.tool.model.lesson.DEFAULT_LESSON_CONTROL_COLOR
import org.cru.godtools.tool.model.lesson.XMLNS_LESSON
import org.cru.godtools.tool.model.lesson.XML_CONTROL_COLOR
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.skipTag

private const val XML_MANIFEST = "manifest"
private const val XML_TOOL = "tool"
private const val XML_LOCALE = "locale"
private const val XML_TYPE = "type"
private const val XML_TYPE_ARTICLE = "article"
private const val XML_TYPE_LESSON = "lesson"
private const val XML_TYPE_TRACT = "tract"
private const val XML_TITLE = "title"

class Manifest : BaseModel, Styles {
    companion object {
        @AndroidColorInt
        internal val DEFAULT_PRIMARY_COLOR = color(59, 164, 219, 1.0)
        @AndroidColorInt
        internal val DEFAULT_PRIMARY_TEXT_COLOR = color(255, 255, 255, 1.0)
    }

    val code: String?
    val locale: PlatformLocale?
    val type: Type

    val dismissListeners: Set<EventId>

    @AndroidColorInt
    override val primaryColor: Color
    @AndroidColorInt
    override val primaryTextColor: Color

    @AndroidColorInt
    internal val lessonControlColor: Color

    private val _title: Text?
    val title: String? get() = _title?.text

    internal constructor(parser: XmlPullParser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_MANIFEST, XML_MANIFEST)

        code = parser.getAttributeValue(XML_TOOL)
        locale = parser.getAttributeValue(XML_LOCALE)?.toLocaleOrNull()
        type = Type.parseOrNull(parser.getAttributeValue(XML_TYPE)) ?: Type.DEFAULT

        dismissListeners = parser.getAttributeValue(XML_DISMISS_LISTENERS).toEventIds().toSet()

        primaryColor = parser.getAttributeValue(XML_PRIMARY_COLOR)?.toColorOrNull() ?: DEFAULT_PRIMARY_COLOR
        primaryTextColor =
            parser.getAttributeValue(XML_PRIMARY_TEXT_COLOR)?.toColorOrNull() ?: DEFAULT_PRIMARY_TEXT_COLOR

        lessonControlColor =
            parser.getAttributeValue(XMLNS_LESSON, XML_CONTROL_COLOR)?.toColorOrNull() ?: DEFAULT_LESSON_CONTROL_COLOR

        var title: Text? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) continue

            when (parser.namespace) {
                XMLNS_MANIFEST -> when (parser.name) {
                    XML_TITLE -> title = parser.parseTextChild(this, XMLNS_MANIFEST, XML_TITLE)
                    else -> parser.skipTag()
                }
                else -> parser.skipTag()
            }
        }

        _title = title
    }

    enum class Type {
        TRACT, ARTICLE, LESSON, UNKNOWN;

        companion object {
            val DEFAULT = TRACT

            fun parseOrNull(value: String?) = when (value) {
                XML_TYPE_ARTICLE -> ARTICLE
                XML_TYPE_LESSON -> LESSON
                XML_TYPE_TRACT -> TRACT
                null -> null
                else -> UNKNOWN
            }
        }
    }
}
