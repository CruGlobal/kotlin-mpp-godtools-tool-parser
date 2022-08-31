package org.cru.godtools.tool.model

import org.cru.godtools.expressions.Expression
import org.cru.godtools.tool.FEATURE_REQUIRED_VERSIONS
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.REGEX_SEQUENCE_SEPARATOR
import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.model.DeviceType.Companion.toDeviceTypes
import org.cru.godtools.tool.model.Version.Companion.toVersionOrNull
import org.cru.godtools.tool.model.tips.InlineTip
import org.cru.godtools.tool.model.tips.Tip
import org.cru.godtools.tool.model.tips.XMLNS_TRAINING
import org.cru.godtools.tool.xml.XmlPullParser

private const val XML_REQUIRED_FEATURES = "required-features"
private const val XML_REQUIRED_ANDROID_VERSION = "required-android-version"
private const val XML_REQUIRED_IOS_VERSION = "required-ios-version"
private const val XML_RESTRICT_TO = "restrictTo"
private const val XML_VERSION = "version"

abstract class Content : BaseModel, Visibility {
    private val version: Int
    private val restrictTo: Set<DeviceType>
    private val requiredFeatures: Set<String>
    private val requiredAndroidVersion: Version?
    private val requiredIosVersion: Version?

    final override val invisibleIf: Expression?
    final override val goneIf: Expression?

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent) {
        version = parser.getAttributeValue(null, XML_VERSION)?.toIntOrNull() ?: SCHEMA_VERSION
        restrictTo = parser.getAttributeValue(XML_RESTRICT_TO)?.toDeviceTypes() ?: DeviceType.ALL
        requiredFeatures = parser.getAttributeValue(XML_REQUIRED_FEATURES)
            ?.split(REGEX_SEQUENCE_SEPARATOR)?.filterTo(mutableSetOf()) { it.isNotBlank() }.orEmpty()
        requiredAndroidVersion = parser.getAttributeValue(XML_REQUIRED_ANDROID_VERSION)
            ?.let { it.toVersionOrNull() ?: Version.MAX }
        requiredIosVersion = parser.getAttributeValue(XML_REQUIRED_IOS_VERSION)
            ?.let { it.toVersionOrNull() ?: Version.MAX }

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
        requiredAndroidVersion: Version? = null,
        requiredIosVersion: Version? = null,
        invisibleIf: Expression? = null,
        goneIf: Expression? = null
    ) : super(parent) {
        this.restrictTo = restrictTo
        this.version = version
        this.requiredFeatures = requiredFeatures
        this.requiredAndroidVersion = requiredAndroidVersion
        this.requiredIosVersion = requiredIosVersion
        this.invisibleIf = invisibleIf
        this.goneIf = goneIf
    }

    /**
     * @return true if this content element should be completely ignored.
     */
    internal open val isIgnored
        get() = version > SCHEMA_VERSION ||
            !areContentRestrictionsSatisfied ||
            requiredFeatures.any { !manifest.config.supportsFeature(it) } ||
            restrictTo.none { it in manifest.config.supportedDeviceTypes } ||
            invisibleIf?.isValid() == false ||
            goneIf?.isValid() == false

    private val areContentRestrictionsSatisfied
        get() = manifest.config.isRequiredVersionSatisfied(DeviceType.ANDROID, requiredAndroidVersion) &&
            manifest.config.isRequiredVersionSatisfied(DeviceType.IOS, requiredIosVersion)

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

private fun ParserConfig.isRequiredVersionSatisfied(deviceType: DeviceType, version: Version?) = when {
    version == null -> true
    !supportsFeature(FEATURE_REQUIRED_VERSIONS) -> false
    deviceType != this.deviceType -> true
    (appVersion ?: Version.MIN) >= version -> true
    else -> false
}
