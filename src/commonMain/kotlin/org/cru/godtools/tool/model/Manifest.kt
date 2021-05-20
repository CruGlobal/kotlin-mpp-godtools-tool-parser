package org.cru.godtools.tool.model

import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_MANIFEST = "manifest"
private const val XML_TYPE_ARTICLE = "article"
private const val XML_TYPE_LESSON = "lesson"
private const val XML_TYPE_TRACT = "tract"

class Manifest {
    internal constructor(parser: XmlPullParser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_MANIFEST, XML_MANIFEST)
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
