package org.cru.godtools.tool.model

import org.cru.godtools.expressions.Expression
import org.cru.godtools.tool.REGEX_SEQUENCE_SEPARATOR
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.model.DeviceType.Companion.toDeviceTypes
import org.cru.godtools.tool.model.tips.InlineTip
import org.cru.godtools.tool.model.tips.Tip
import org.cru.godtools.tool.model.tips.XMLNS_TRAINING
import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_REQUIRED_FEATURES = "required-features"
private const val XML_RESTRICT_TO = "restrictTo"
private const val XML_VERSION = "version"

abstract class Content : BaseModel, Visibility {
    private val version: Int
    private val restrictTo: Set<DeviceType>
    private val requiredFeatures: Set<String>

    final override val invisibleIf: Expression?
    final override val goneIf: Expression?

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent) {
        version = parser.getAttributeValue(null, XML_VERSION)?.toIntOrNull() ?: SCHEMA_VERSION
        restrictTo = parser.getAttributeValue(XML_RESTRICT_TO)?.toDeviceTypes() ?: DeviceType.ALL
        requiredFeatures = parser.getAttributeValue(XML_REQUIRED_FEATURES)
            ?.split(REGEX_SEQUENCE_SEPARATOR)?.filterTo(mutableSetOf()) { it.isNotBlank() }.orEmpty()

        parser.parseVisibilityAttrs { invisibleIf, goneIf ->
            this.invisibleIf = invisibleIf
            this.goneIf = goneIf
        }
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(
        parent: Base = Manifest(),
        version: Int = SCHEMA_VERSION,
        restrictTo: Set<DeviceType> = DeviceType.ALL,
        requiredFeatures: Set<String> = emptySet(),
        invisibleIf: Expression? = null,
        goneIf: Expression? = null
    ) : super(parent) {
        this.restrictTo = restrictTo
        this.version = version
        this.requiredFeatures = requiredFeatures
        this.invisibleIf = invisibleIf
        this.goneIf = goneIf
    }

    /**
     * @return true if this content element should be completely ignored.
     */
    internal open val isIgnored
        get() = version > SCHEMA_VERSION ||
            !manifest.config.supportedFeatures.containsAll(requiredFeatures) ||
            restrictTo.none { it in manifest.config.supportedDeviceTypes } ||
            invisibleIf?.isValid() == false ||
            goneIf?.isValid() == false

    open val tips get() = emptyList<Tip>()

    internal companion object {
        internal fun XmlPullParser.parseContentElement(parent: Base): Content? {
            require(XmlPullParser.START_TAG, null, null)
            return when (namespace) {
                XMLNS_CONTENT -> when (name) {
                    Accordion.XML_ACCORDION -> Accordion(parent, this)
                    Animation.XML_ANIMATION -> Animation(parent, this)
                    Button.XML_BUTTON -> Button(parent, this)
                    Card.XML_CARD -> Card(parent, this)
                    Fallback.XML_FALLBACK -> Fallback(parent, this).content.firstOrNull()
                    Flow.XML_FLOW -> Flow(parent, this)
                    Form.XML_FORM -> Form(parent, this)
                    Image.XML_IMAGE -> Image(parent, this)
                    Input.XML_INPUT -> Input(parent, this)
                    Link.XML_LINK -> Link(parent, this)
                    Multiselect.XML_MULTISELECT -> Multiselect(parent, this)
                    Paragraph.XML_PARAGRAPH ->
                        when (getAttributeValue(Paragraph.XML_FALLBACK)?.toBoolean()) {
                            true -> Fallback(parent, this).content.firstOrNull()
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

// HACK: This is a workaround for a Kotlin/JS IR compiler issue when running tests. This should be fixed in Kotlin 1.6
@get:RestrictTo(RestrictToScope.TESTS)
internal val Content.testIsIgnored get() = isIgnored
