package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.XmlPullParserException

class Fallback : Content, Parent {
    internal companion object {
        internal const val XML_FALLBACK = "fallback"
    }

    private val _content: List<Content>
    override val content get() = _content.take(1)
    override val tips get() = contentTips

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        when (parser.name) {
            Paragraph.XML_PARAGRAPH -> {
                parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, Paragraph.XML_PARAGRAPH)
                if (parser.getAttributeValue(Paragraph.XML_FALLBACK)?.toBoolean() != true) {
                    throw XmlPullParserException("expected fallback=\"true\"")
                }
            }
            else -> parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_FALLBACK)
        }

        _content = parseContent(parser)
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(parent: Base = Manifest(), content: ((Fallback) -> List<Content>)? = null) : super(parent) {
        _content = content?.invoke(this).orEmpty()
    }
}
