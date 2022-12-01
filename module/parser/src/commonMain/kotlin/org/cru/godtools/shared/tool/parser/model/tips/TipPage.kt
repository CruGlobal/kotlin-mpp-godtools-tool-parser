package org.cru.godtools.shared.tool.parser.model.tips

import org.cru.godtools.shared.tool.parser.model.BaseModel
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.Parent
import org.cru.godtools.shared.tool.parser.model.parseContent
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser

class TipPage : BaseModel, Parent {
    internal companion object {
        internal const val XML_PAGE = "page"
    }

    private val tip: Tip
    val position: Int

    override val content: List<Content>

    internal constructor(tip: Tip, position: Int, parser: XmlPullParser) : super(tip) {
        parser.require(XmlPullParser.START_TAG, XMLNS_TRAINING, XML_PAGE)
        this.tip = tip
        this.position = position
        content = parseContent(parser)
    }

    val isLastPage get() = position == tip.pages.size - 1
}