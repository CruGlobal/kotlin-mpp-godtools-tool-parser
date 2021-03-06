package org.cru.godtools.tool.model

import org.cru.godtools.tool.REGEX_SEQUENCE_SEPARATOR
import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.AndroidDimension
import org.cru.godtools.tool.internal.DP
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.tool.model.Text.Align.Companion.toTextAlignOrNull
import org.cru.godtools.tool.model.Text.Style.Companion.toTextStyles
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_START_IMAGE = "start-image"
private const val XML_START_IMAGE_SIZE = "start-image-size"
private const val XML_END_IMAGE = "end-image"
private const val XML_END_IMAGE_SIZE = "end-image-size"
private const val XML_TEXT_ALIGN = "text-align"
private const val XML_TEXT_ALIGN_START = "start"
private const val XML_TEXT_ALIGN_CENTER = "center"
private const val XML_TEXT_ALIGN_END = "end"
private const val XML_TEXT_STYLE = "text-style"
private const val XML_TEXT_STYLE_BOLD = "bold"
private const val XML_TEXT_STYLE_ITALIC = "italic"
private const val XML_TEXT_STYLE_UNDERLINE = "underline"

class Text : Content {
    internal companion object {
        internal const val XML_TEXT = "text"

        @VisibleForTesting
        @AndroidDimension(unit = DP)
        internal const val DEFAULT_IMAGE_SIZE = 40
    }

    val text: String?

    private val _textAlign: Align?
    val textAlign get() = _textAlign ?: stylesParent.textAlign
    @AndroidColorInt
    private val _textColor: PlatformColor?
    @get:AndroidColorInt
    val textColor get() = _textColor ?: stylesParent.textColor
    private val _textScale: Double
    val textScale get() = _textScale * stylesParent.textScale
    val textStyles: Set<Style>

    @VisibleForTesting
    internal val startImageName: String?
    val startImage get() = getResource(startImageName)
    @AndroidDimension(unit = DP)
    val startImageSize: Int
    @VisibleForTesting
    internal val endImageName: String?
    val endImage get() = getResource(endImageName)
    @AndroidDimension(unit = DP)
    val endImageSize: Int

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_TEXT)

        _textAlign = parser.getAttributeValue(XML_TEXT_ALIGN)?.toTextAlignOrNull()
        _textColor = parser.getAttributeValue(XML_TEXT_COLOR)?.toColorOrNull()
        _textScale = parser.getAttributeValue(XML_TEXT_SCALE)?.toDoubleOrNull() ?: DEFAULT_TEXT_SCALE
        textStyles = parser.getAttributeValue(XML_TEXT_STYLE)?.toTextStyles().orEmpty()

        startImageName = parser.getAttributeValue(XML_START_IMAGE)
        startImageSize = parser.getAttributeValue(XML_START_IMAGE_SIZE)?.toIntOrNull() ?: DEFAULT_IMAGE_SIZE
        endImageName = parser.getAttributeValue(XML_END_IMAGE)
        endImageSize = parser.getAttributeValue(XML_END_IMAGE_SIZE)?.toIntOrNull() ?: DEFAULT_IMAGE_SIZE

        text = parser.nextText()
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    constructor(
        parent: Base,
        text: String? = null,
        textScale: Double = DEFAULT_TEXT_SCALE,
        @AndroidColorInt textColor: PlatformColor? = null,
        textAlign: Align? = null,
        textStyles: Set<Style> = emptySet(),
        startImage: String? = null,
        endImage: String? = null,
    ) : super(parent) {
        this.text = text
        _textAlign = textAlign
        _textColor = textColor
        _textScale = textScale
        this.textStyles = textStyles
        startImageName = startImage
        startImageSize = DEFAULT_IMAGE_SIZE
        endImageName = endImage
        endImageSize = DEFAULT_IMAGE_SIZE
    }

    enum class Align {
        START, CENTER, END;

        internal companion object {
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

val Text?.textAlign get() = this?.textAlign ?: stylesParent.textAlign
@get:AndroidColorInt
val Text?.textColor get() = this?.textColor ?: stylesParent.textColor
val Text?.textScale get() = this?.textScale ?: stylesParent.textScale

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
