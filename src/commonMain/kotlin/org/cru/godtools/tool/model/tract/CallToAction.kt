package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidColorInt
import org.cru.godtools.tool.internal.VisibleForTesting
import org.cru.godtools.tool.model.BaseModel
import org.cru.godtools.tool.model.Color
import org.cru.godtools.tool.model.EventId
import org.cru.godtools.tool.model.Text
import org.cru.godtools.tool.model.XML_EVENTS
import org.cru.godtools.tool.model.parseTextChild
import org.cru.godtools.tool.model.primaryColor
import org.cru.godtools.tool.model.stylesParent
import org.cru.godtools.tool.model.tips.XMLNS_TRAINING
import org.cru.godtools.tool.model.toColorOrNull
import org.cru.godtools.tool.model.toEventIds
import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_CONTROL_COLOR = "control-color"
private const val XML_TIP = "tip"

class CallToAction : BaseModel {
    companion object {
        internal const val XML_CALL_TO_ACTION = "call-to-action"
    }

    private val page: TractPage

    val label: Text?
    val events: List<EventId>

    @AndroidColorInt
    private val _controlColor: Color?
    @get:AndroidColorInt
    val controlColor get() = _controlColor ?: stylesParent.primaryColor

    @VisibleForTesting
    internal val tipId: String?
    val tip get() = manifest.findTip(tipId)

    internal constructor(parent: TractPage) : super(parent) {
        page = parent
        label = null
        events = emptyList()
        _controlColor = null
        tipId = null
    }

    internal constructor(parent: TractPage, parser: XmlPullParser) : super(parent) {
        parser.require(XmlPullParser.START_TAG, XMLNS_TRACT, XML_CALL_TO_ACTION)

        page = parent
        events = parser.getAttributeValue(XML_EVENTS).toEventIds()
        _controlColor = parser.getAttributeValue(XML_CONTROL_COLOR)?.toColorOrNull()
        tipId = parser.getAttributeValue(XMLNS_TRAINING, XML_TIP)

        label = parser.parseTextChild(this, XMLNS_TRACT, XML_CALL_TO_ACTION)
    }
}

@get:AndroidColorInt
val CallToAction?.controlColor get() = this?.controlColor ?: stylesParent.primaryColor
