package org.cru.godtools.shared.tool.analytics

import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.cru.godtools.shared.tool.parser.model.page.CardCollectionPage
import org.cru.godtools.shared.tool.parser.model.page.Page
import org.cru.godtools.shared.tool.parser.model.tips.TipPage
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

object ToolAnalyticsScreenNames {
    private const val CYOA_PAGE_SEPARATOR = ":"

    fun forCyoaPage(page: Page) = "${page.manifest.code.orEmpty()}$CYOA_PAGE_SEPARATOR${page.id}"
    fun forCyoaCardCollectionCard(card: CardCollectionPage.Card) =
        "${forCyoaPage(card.page)}$CYOA_PAGE_SEPARATOR${card.id}"

    fun forLessonPage(page: LessonPage) = "${page.manifest.code}-${page.position}"

    fun forTractPage(page: TractPage, card: TractPage.Card? = null) = buildString {
        append(page.manifest.code).append('-').append(page.position)
        when (val pos = card?.position) {
            null -> Unit
            // convert card index to letter 'a'-'z'
            in 0..25 -> append((97 + pos).toChar())
            else -> append('-').append(pos)
        }
    }

    fun forTipPage(page: TipPage) = forTipPage(page.manifest.code.orEmpty(), page.tip.id, page.position)
    fun forTipPage(tool: String, tipId: String, page: Int) = "$tool-tip-$tipId-$page"
}
