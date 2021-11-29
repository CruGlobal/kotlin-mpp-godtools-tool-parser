package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.RestrictTo
import org.cru.godtools.tool.internal.RestrictToScope
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren

private const val XML_ID = "id"
private const val XML_LABEL = "label"
private const val XML_BANNER = "banner"
private const val XML_AEM_TAG = "aem-tag"

class Category : BaseModel, Styles {
    internal companion object {
        internal const val XML_CATEGORY = "category"
    }

    val id: String?
    val label: Text?
    val aemTags: Set<String>
    private val _banner: String?
    val banner get() = getResource(_banner)

    override val textColor get() = manifest.categoryLabelColor

    internal constructor(manifest: Manifest, parser: XmlPullParser) : super(manifest) {
        parser.require(XmlPullParser.START_TAG, XMLNS_MANIFEST, XML_CATEGORY)

        id = parser.getAttributeValue(XML_ID)
        _banner = parser.getAttributeValue(XML_BANNER)

        var label: Text? = null
        aemTags = buildSet {
            parser.parseChildren {
                when (parser.namespace) {
                    XMLNS_MANIFEST -> when (parser.name) {
                        XML_LABEL -> label = parser.parseTextChild(this@Category, XMLNS_MANIFEST, XML_LABEL)
                    }
                    XMLNS_ARTICLE -> when (parser.name) {
                        XML_AEM_TAG -> parser.getAttributeValue(XML_ID)?.let { add(it) }
                    }
                }
            }
        }
        this.label = label
    }

    @RestrictTo(RestrictToScope.TESTS)
    internal constructor(manifest: Manifest = Manifest(), label: (Base) -> Text?) : super(manifest) {
        id = null
        this.label = label(this)
        aemTags = emptySet()
        _banner = null
    }
}
