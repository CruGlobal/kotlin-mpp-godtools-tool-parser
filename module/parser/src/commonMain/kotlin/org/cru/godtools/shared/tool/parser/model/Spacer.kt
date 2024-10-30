package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.RestrictTo
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import org.cru.godtools.shared.tool.parser.model.Spacer.Mode.Companion.toModeOrNull
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.skipTag

private const val XML_HEIGHT = "height"
private const val XML_MODE = "mode"
private const val XML_MODE_AUTO = "auto"
private const val XML_MODE_FIXED = "fixed"

@JsExport
@OptIn(ExperimentalJsExport::class)
class Spacer : Content {
    internal companion object {
        internal const val XML_SPACER = "spacer"
    }

    val mode: Mode
    val height: Int

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_SPACER)

        mode = parser.getAttributeValue(XML_MODE)?.toModeOrNull() ?: Mode.DEFAULT
        height = parser.getAttributeValue(XML_HEIGHT)?.toIntOrNull()?.coerceAtLeast(0) ?: 0

        parser.skipTag()
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    @JsName("createTestSpacer")
    constructor(parent: Base = Manifest(), mode: Mode = Mode.AUTO, height: Int = 0) : super(parent) {
        this.mode = mode
        this.height = height
    }

    enum class Mode {
        AUTO,
        FIXED;

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
