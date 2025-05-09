package org.cru.godtools.shared.tool.parser.model.tract

import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import com.github.ajalt.colormath.Color
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import org.cru.godtools.shared.tool.parser.internal.AndroidColorInt
import org.cru.godtools.shared.tool.parser.internal.toColorOrNull
import org.cru.godtools.shared.tool.parser.model.BaseModel
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.parseTextChild
import org.cru.godtools.shared.tool.parser.model.primaryColor
import org.cru.godtools.shared.tool.parser.model.stylesParent
import org.cru.godtools.shared.tool.parser.model.tips.XMLNS_TRAINING
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser

private const val XML_CONTROL_COLOR = "control-color"
private const val XML_TIP = "tip"

@JsExport
@OptIn(ExperimentalJsExport::class)
class CallToAction : BaseModel {
    internal companion object {
        internal const val XML_CALL_TO_ACTION = "call-to-action"
    }

    private val page: TractPage

    val label: Text?

    @Suppress("ktlint:standard:property-naming") // https://github.com/pinterest/ktlint/issues/2448
    private val _controlColor: Color?
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

    @JsName("createTestCallToAction")
    @RestrictTo(RestrictTo.Scope.TESTS)
    constructor(
        page: TractPage = TractPage(),
        label: ((CallToAction) -> Text)? = null,
        controlColor: Color? = null,
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
