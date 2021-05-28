package org.cru.godtools.tool.model

import org.cru.godtools.tool.REGEX_SEQUENCE_SEPARATOR
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_TEXT_ALIGN_START = "start"
private const val XML_TEXT_ALIGN_CENTER = "center"
private const val XML_TEXT_ALIGN_END = "end"
private const val XML_TEXT_STYLE_BOLD = "bold"
private const val XML_TEXT_STYLE_ITALIC = "italic"
private const val XML_TEXT_STYLE_UNDERLINE = "underline"

class Text : Content {
    companion object {
        internal const val XML_TEXT = "text"
    }

    val text: String?

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_TEXT)
        text = parser.nextText()
    }

    enum class Align {
        START, CENTER, END;

        internal companion object {
            val DEFAULT = START

            fun String.toTextAlignOrNull() = when (this) {
                XML_TEXT_ALIGN_START -> START
                XML_TEXT_ALIGN_CENTER -> CENTER
                XML_TEXT_ALIGN_END -> END
                else -> null
            }
        }
    }

    enum class Style {
        BOLD, ITALIC, UNDERLINE;

        internal companion object {
            private fun String.toTextStyleOrNull() = when (this) {
                XML_TEXT_STYLE_BOLD -> BOLD
                XML_TEXT_STYLE_ITALIC -> ITALIC
                XML_TEXT_STYLE_UNDERLINE -> UNDERLINE
                else -> null
            }

            fun String.toTextStyles() =
                REGEX_SEQUENCE_SEPARATOR.split(this).mapNotNullTo(mutableSetOf()) { it.toTextStyleOrNull() }
        }
    }
}

internal fun XmlPullParser.parseTextChild(
    parent: Base,
    parentNamespace: String?,
    parentName: String,
    block: () -> Unit = { }
): Text? {
    require(XmlPullParser.START_TAG, parentNamespace, parentName)

    // process any child elements
    var text: Text? = null
    parseChildren {
        // execute any custom parsing logic from the call-site
        // if the block consumes the tag, the parser will be on an END_TAG after returning
        block()
        if (eventType == XmlPullParser.END_TAG) return@parseChildren

        when (namespace) {
            XMLNS_CONTENT -> when (name) {
                Text.XML_TEXT -> text = Text(parent, this)
            }
        }
    }
    return text
}
