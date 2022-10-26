package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.BaseModel
import org.cru.godtools.tool.model.PlatformColor
import org.cru.godtools.tool.model.Text
import org.cru.godtools.tool.model.parseTextChild
import org.cru.godtools.tool.model.primaryColor
import org.cru.godtools.tool.model.stylesParent
import org.cru.godtools.tool.model.tips.XMLNS_TRAINING
import org.cru.godtools.tool.model.toColorOrNull
import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_CONTROL_COLOR = "control-color"
private const val XML_TIP = "tip"

class CallToAction : BaseModel {
    internal companion object {
        internal const val XML_CALL_TO_ACTION = "call-to-action"
    }

    private val page: TractPage

    val label: Text?

    @AndroidColorInt
    private val _controlColor: PlatformColor?
    @get:AndroidColorInt
    internal val controlColor get() = _controlColor ?: stylesParent.primaryColor

    @VisibleForTesting
    internal val tipId: String?
    val tip get() = manifest.findTip(tipId)

    internal constructor(parent: TractPage) : super(parent) {
        page = parent
        label = null
        _controlColor = null
        tipId = null
    }

    internal constructor(page: TractPage, parser: XmlPullParser) : super(page) {
        parser.require(XmlPullParser.START_TAG, XMLNS_TRACT, XML_CALL_TO_ACTION)

        this.page = page
        _controlColor = parser.getAttributeValue(XML_CONTROL_COLOR)?.toColorOrNull()
        tipId = parser.getAttributeValue(XMLNS_TRAINING, XML_TIP)

        label = parser.parseTextChild(this, XMLNS_TRACT, XML_CALL_TO_ACTION)
    }

    @RestrictTo(RestrictToScope.TESTS)
    constructor(
        page: TractPage = TractPage(),
        label: ((CallToAction) -> Text)? = null,
        @AndroidColorInt controlColor: PlatformColor? = null,
        tip: String? = null
    ) : super(page) {
        this.page = page
        this.label = label?.invoke(this)
        _controlColor = controlColor
        tipId = tip
    }
}

@get:AndroidColorInt
val CallToAction?.controlColor get() = this?.controlColor ?: stylesParent.primaryColor
