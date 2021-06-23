package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.Base
import org.cru.godtools.tool.model.BaseModel
import org.cru.godtools.tool.model.Color
import org.cru.godtools.tool.model.Styles
import org.cru.godtools.tool.model.TEXT_SIZE_BASE
import org.cru.godtools.tool.model.TEXT_SIZE_HEADER
import org.cru.godtools.tool.model.TEXT_SIZE_HEADER_NUMBER
import org.cru.godtools.tool.model.Text
import org.cru.godtools.tool.model.XML_BACKGROUND_COLOR
import org.cru.godtools.tool.model.parseTextChild
import org.cru.godtools.tool.model.primaryColor
import org.cru.godtools.tool.model.stylesOverride
import org.cru.godtools.tool.model.stylesParent
import org.cru.godtools.tool.model.tips.XMLNS_TRAINING
import org.cru.godtools.tool.model.toColorOrNull
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_NUMBER = "number"
private const val XML_TITLE = "title"
private const val XML_TIP = "tip"

class Header : BaseModel, Styles {
    internal companion object {
        internal const val XML_HEADER = "header"
    }

    @AndroidColorInt
    private val _backgroundColor: Color?
    @get:AndroidColorInt
    internal val backgroundColor get() = _backgroundColor ?: primaryColor

    @get:AndroidColorInt
    override val textColor get() = primaryTextColor
    override val textScale get() = super.textScale * TEXT_SIZE_HEADER / TEXT_SIZE_BASE

    private val numberParent by lazy {
        stylesOverride(textScale = TEXT_SIZE_HEADER_NUMBER.toDouble() / TEXT_SIZE_HEADER)
    }
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
                    XML_NUMBER -> number = parser.parseTextChild(numberParent, XMLNS_TRACT, XML_NUMBER)
                    XML_TITLE -> title = parser.parseTextChild(this, XMLNS_TRACT, XML_TITLE)
                }
            }
        }
        this.number = number
        this.title = title
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        page: TractPage = TractPage(),
        backgroundColor: Color? = null,
        tip: String? = null,
        number: ((Base) -> Text?)? = null,
        title: ((Base) -> Text?)? = null,
    ) : super(page) {
        _backgroundColor = backgroundColor

        this.number = number?.invoke(numberParent)
        this.title = title?.invoke(this)

        tipId = tip
    }
}

val Header?.backgroundColor get() = this?.backgroundColor ?: stylesParent.primaryColor
