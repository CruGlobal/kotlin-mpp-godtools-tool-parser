package org.cru.godtools.shared.tool.parser.model.tips

import androidx.annotation.RestrictTo
import org.cru.godtools.shared.tool.parser.model.BaseModel
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Styles
import org.cru.godtools.shared.tool.parser.model.tips.Tip.Type.Companion.toTypeOrNull
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.parseChildren

private const val XML_TIP = "tip"
private const val XML_TYPE = "type"
private const val XML_TYPE_TIP = "tip"
private const val XML_TYPE_ASK = "ask"
private const val XML_TYPE_CONSIDER = "consider"
private const val XML_TYPE_PREPARE = "prepare"
private const val XML_TYPE_QUOTE = "quote"
private const val XML_PAGES = "pages"

class Tip : BaseModel, Styles {
    val id: String
    val type: Type

    override val primaryColor get() = Manifest.DEFAULT_PRIMARY_COLOR
    override val primaryTextColor get() = Manifest.DEFAULT_PRIMARY_TEXT_COLOR
    override val textColor get() = Manifest.DEFAULT_TEXT_COLOR

    val pages: List<TipPage>

    internal constructor(manifest: Manifest, id: String, parser: XmlPullParser) : super(manifest) {
        parser.require(XmlPullParser.START_TAG, XMLNS_TRAINING, XML_TIP)

        this.id = id
        type = parser.getAttributeValue(XML_TYPE)?.toTypeOrNull() ?: Type.DEFAULT
        pages = buildList {
            parser.parseChildren {
                when (parser.namespace) {
                    XMLNS_TRAINING -> when (parser.name) {
                        XML_PAGES -> addAll(parser.parsePages())
                    }
                }
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    constructor(manifest: Manifest? = null, id: String = "", type: Type = Type.DEFAULT) : super(manifest) {
        this.id = id
        this.type = type
        pages = emptyList()
    }

    private fun XmlPullParser.parsePages() = buildList {
        require(XmlPullParser.START_TAG, XMLNS_TRAINING, XML_PAGES)
        parseChildren {
            when (namespace) {
                XMLNS_TRAINING -> when (name) {
                    TipPage.XML_PAGE -> add(TipPage(this@Tip, size, this@parsePages))
                }
            }
        }
    }

    enum class Type {
        ASK,
        CONSIDER,
        TIP,
        PREPARE,
        QUOTE;

        internal companion object {
            internal val DEFAULT = TIP

            internal fun String.toTypeOrNull() = when (this) {
                XML_TYPE_TIP -> TIP
                XML_TYPE_ASK -> ASK
                XML_TYPE_CONSIDER -> CONSIDER
                XML_TYPE_PREPARE -> PREPARE
                XML_TYPE_QUOTE -> QUOTE
                else -> null
            }
        }
    }
}

val Tip?.textColor get() = this?.textColor ?: Manifest.DEFAULT_TEXT_COLOR
