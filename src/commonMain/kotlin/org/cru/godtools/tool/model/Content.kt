package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.model.DeviceType.Companion.toDeviceTypes
import org.cru.godtools.tool.model.tips.InlineTip
import org.cru.godtools.tool.model.tips.Tip
import org.cru.godtools.tool.model.tips.XMLNS_TRAINING
import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_RESTRICT_TO = "restrictTo"
private const val XML_VERSION = "version"

abstract class Content : BaseModel {
    private val version: Int
    private val restrictTo: Set<DeviceType>

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent) {
        version = parser.getAttributeValue(null, XML_VERSION)?.toIntOrNull() ?: SCHEMA_VERSION
        restrictTo = parser.getAttributeValue(XML_RESTRICT_TO)?.toDeviceTypes() ?: DeviceType.ALL
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    internal constructor(
        parent: Base,
        version: Int = SCHEMA_VERSION,
        restrictTo: Set<DeviceType> = DeviceType.ALL
    ) : super(parent) {
        this.restrictTo = restrictTo
        this.version = version
    }

    /**
     * @return true if this content element should be completely ignored.
     */
    open val isIgnored get() = version > SCHEMA_VERSION || restrictTo.none { it in DeviceType.SUPPORTED }

    open val tips get() = emptyList<Tip>()

    internal companion object {
        internal fun XmlPullParser.parseContentElement(parent: Base): Content? {
            require(XmlPullParser.START_TAG, null, null)
            return when (namespace) {
                XMLNS_CONTENT -> when (name) {
                    Accordion.XML_ACCORDION -> Accordion(parent, this)
                    Animation.XML_ANIMATION -> Animation(parent, this)
                    Button.XML_BUTTON -> Button(parent, this)
                    Fallback.XML_FALLBACK -> Fallback(parent, this)
                    Form.XML_FORM -> Form(parent, this)
                    Image.XML_IMAGE -> Image(parent, this)
                    Input.XML_INPUT -> Input(parent, this)
                    Link.XML_LINK -> Link(parent, this)
                    Paragraph.XML_PARAGRAPH ->
                        when (getAttributeValue(Paragraph.XML_FALLBACK)?.toBoolean()) {
                            true -> Fallback(parent, this)
                            else -> Paragraph(parent, this)
                        }
                    Spacer.XML_SPACER -> Spacer(parent, this)
                    Tabs.XML_TABS -> Tabs(parent, this)
                    Text.XML_TEXT -> Text(parent, this)
                    Video.XML_VIDEO -> Video(parent, this)
                    else -> null
                }
                XMLNS_TRAINING -> when (name) {
                    InlineTip.XML_TIP -> InlineTip(parent, this)
                    else -> null
                }
                else -> null
            }
        }
    }
}
