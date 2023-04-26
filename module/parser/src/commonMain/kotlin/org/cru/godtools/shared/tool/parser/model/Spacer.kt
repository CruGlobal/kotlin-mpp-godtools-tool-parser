package org.cru.godtools.shared.tool.parser.model

import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.tool.parser.model.Spacer.Mode.Companion.toModeOrNull
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.skipTag

private const val XML_HEIGHT = "height"
private const val XML_MODE = "mode"
private const val XML_MODE_AUTO = "auto"
private const val XML_MODE_FIXED = "fixed"

class Spacer : Content {
    internal companion object {
        private const val CONTENT_TYPE = "spacer"
        internal const val XML_SPACER = "spacer"
    }

    val mode: Mode
    val height: Int

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, CONTENT_TYPE, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_SPACER)

        mode = parser.getAttributeValue(XML_MODE)?.toModeOrNull() ?: Mode.DEFAULT
        height = parser.getAttributeValue(XML_HEIGHT)?.toIntOrNull()?.coerceAtLeast(0) ?: 0

        parser.skipTag()
    }

    @RestrictTo(RestrictToScope.TESTS)
    constructor(parent: Base, mode: Mode = Mode.AUTO, height: Int = 0) : super(parent, CONTENT_TYPE) {
        this.mode = mode
        this.height = height
    }

    enum class Mode {
        AUTO, FIXED;

        internal companion object {
            internal val DEFAULT = AUTO

            internal fun String.toModeOrNull() = when (this) {
                XML_MODE_AUTO -> AUTO
                XML_MODE_FIXED -> FIXED
                else -> null
            }
        }
    }
}
