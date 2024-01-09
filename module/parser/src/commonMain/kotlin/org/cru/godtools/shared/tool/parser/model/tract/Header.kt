package org.cru.godtools.shared.tool.parser.model.tract

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.ccci.gto.support.androidx.annotation.VisibleForTesting
import org.cru.godtools.shared.tool.parser.internal.AndroidColorInt
import org.cru.godtools.shared.tool.parser.model.BaseModel
import org.cru.godtools.shared.tool.parser.model.PlatformColor
import org.cru.godtools.shared.tool.parser.model.Styles
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.XML_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.parseTextChild
import org.cru.godtools.shared.tool.parser.model.primaryColor
import org.cru.godtools.shared.tool.parser.model.stylesParent
import org.cru.godtools.shared.tool.parser.model.tips.XMLNS_TRAINING
import org.cru.godtools.shared.tool.parser.model.toColorOrNull
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_NUMBER = "number"
private const val XML_TITLE = "title"
private const val XML_TIP = "tip"

@JsExport
@OptIn(ExperimentalJsExport::class)
class Header : BaseModel, Styles {
    internal companion object {
        internal const val XML_HEADER = "header"
    }

    @AndroidColorInt
    @Suppress("ktlint:standard:property-naming") // https://github.com/pinterest/ktlint/issues/2448
    private val _backgroundColor: PlatformColor?
    @get:AndroidColorInt
    internal val backgroundColor get() = _backgroundColor ?: primaryColor

    @get:AndroidColorInt
    override val textColor get() = primaryTextColor

    val number: Text?
    val title: Text?

    @VisibleForTesting
    internal val tipId: String?
    val tip get() = manifest.findTip(tipId)

    internal constructor(page: TractPage, parser: XmlPullParser) : super(page) {
        parser.require(XmlPullParser.START_TAG, XMLNS_TRACT, XML_HEADER)

        _backgroundColor = parser.getAttributeValue(XML_BACKGROUND_COLOR)?.toColorOrNull()
        tipId = parser.getAttributeValue(XMLNS_TRAINING, XML_TIP)

        var number: Text? = null
        var title: Text? = null
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_TRACT -> when (parser.name) {
                    XML_NUMBER -> number = parser.parseTextChild(this, XMLNS_TRACT, XML_NUMBER)
                    XML_TITLE -> title = parser.parseTextChild(this, XMLNS_TRACT, XML_TITLE)
                }
            }
        }
        this.number = number
        this.title = title
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(
        page: TractPage = TractPage(),
        backgroundColor: PlatformColor? = null,
        tip: String? = null
    ) : super(page) {
        _backgroundColor = backgroundColor

        number = null
        title = null

        tipId = tip
    }
}

val Header?.backgroundColor get() = this?.backgroundColor ?: stylesParent.primaryColor
