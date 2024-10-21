package org.cru.godtools.shared.tool.parser.model.page

import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_PAGE_COLLECTION
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.HasPages
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.PlatformColor
import org.cru.godtools.shared.tool.parser.model.TestColors
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

@RunOnAndroidWith(AndroidJUnit4::class)
class PageTest : UsesResources("model/page") {
    // region Page.parse()
    @Test
    fun testParseCardCollectionPage() = runTest {
        assertIs<CardCollectionPage>(
            Page.parse(Manifest(type = Manifest.Type.CYOA), null, getTestXmlParser("page_cardcollection.xml"))
        )
        assertNull(Page.parse(Manifest(type = Manifest.Type.LESSON), null, getTestXmlParser("page_cardcollection.xml")))
    }

    @Test
    fun testParseContentPage() = runTest {
        assertIs<ContentPage>(
            Page.parse(Manifest(type = Manifest.Type.CYOA), null, getTestXmlParser("page_content.xml"))
        )
        assertNull(Page.parse(Manifest(type = Manifest.Type.LESSON), null, getTestXmlParser("page_content.xml")))
    }

    @Test
    fun testParseLessonPage() = runTest {
        assertIs<LessonPage>(
            Page.parse(Manifest(type = Manifest.Type.LESSON), null, getTestXmlParser("../lesson/page.xml"))
        )
        assertNull(Page.parse(Manifest(type = Manifest.Type.TRACT), null, getTestXmlParser("../lesson/page.xml")))
    }

    @Test
    fun testParsePageCollectionPage() = runTest {
        val config = ParserConfig().withSupportedFeatures(FEATURE_PAGE_COLLECTION)
        assertIs<PageCollectionPage>(
            Page.parse(
                Manifest(config = config, type = Manifest.Type.CYOA),
                null,
                getTestXmlParser("page_page-collection.xml"),
                parseFile,
            ),
        )
        assertNull(
            Page.parse(
                Manifest(type = Manifest.Type.CYOA),
                null,
                getTestXmlParser("page_page-collection.xml"),
                parseFile,
            ),
        )
        assertNull(
            Page.parse(
                Manifest(config = config, type = Manifest.Type.LESSON),
                null,
                getTestXmlParser("page_page-collection.xml"),
                parseFile,
            ),
        )
    }

    @Test
    fun testParseTractPage() = runTest {
        assertIs<TractPage>(
            Page.parse(Manifest(type = Manifest.Type.TRACT), null, getTestXmlParser("../tract/page.xml"))
        )
        assertNull(Page.parse(Manifest(type = Manifest.Type.LESSON), null, getTestXmlParser("../tract/page.xml")))
    }

    @Test
    fun testParseInvalidPageType() = runTest {
        Manifest.Type.values().forEach {
            assertNull(Page.parse(Manifest(type = it), null, getTestXmlParser("page_invalid_type.xml")))
        }
    }

    @Test
    fun testParseInvalidPageNamespace() = runTest {
        Manifest.Type.values().forEach {
            assertNull(Page.parse(Manifest(type = it), null, getTestXmlParser("page_invalid_namespace.xml")))
        }
    }
    // endregion Page.parse()

    // region Property: position
    @Test
    fun testPosition_manifest() {
        val manifest = Manifest(
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2")) }
        )

        val page1 = manifest.findPage("page1")!!
        val page2 = manifest.findPage("page2")!!
        assertEquals(0, page1.position)
        assertEquals(1, page2.position)
    }

    @Test
    fun testPosition_hasPagesParent() {
        val parent = TestPage(
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2")) }
        )

        val page1 = parent.findPage("page1")!!
        val page2 = parent.findPage("page2")!!
        assertEquals(0, page1.position)
        assertEquals(1, page2.position)
    }
    // endregion Property: position

    // region Property: cardBackgroundColor
    @Test
    fun testPropertyCardBackgroundColor() {
        val manifest = Manifest(cardBackgroundColor = TestColors.GREEN)
        val hasPagesParent = TestPage(parent = manifest, cardBackgroundColor = TestColors.BLUE)

        assertEquals(TestColors.RED, TestPage(manifest, cardBackgroundColor = TestColors.RED).cardBackgroundColor)
        assertEquals(TestColors.RED, TestPage(hasPagesParent, cardBackgroundColor = TestColors.RED).cardBackgroundColor)
        assertEquals(TestColors.GREEN, TestPage(manifest, cardBackgroundColor = null).cardBackgroundColor)
        assertEquals(TestColors.GREEN, TestPage(TestPage(manifest, cardBackgroundColor = null)).cardBackgroundColor)
        assertEquals(TestColors.BLUE, TestPage(hasPagesParent, cardBackgroundColor = null).cardBackgroundColor)
    }
    // endregion Property: cardBackgroundColor

    // region Property: controlColor
    @Test
    fun testPropertyControlColor() {
        val manifest = Manifest(pageControlColor = TestColors.GREEN)
        val hasPagesParent = TestPage(parent = manifest, controlColor = TestColors.BLUE)

        assertEquals(TestColors.RED, TestPage(manifest, controlColor = TestColors.RED).controlColor)
        assertEquals(TestColors.RED, TestPage(hasPagesParent, controlColor = TestColors.RED).controlColor)
        assertEquals(TestColors.GREEN, TestPage(manifest, controlColor = null).controlColor)
        assertEquals(TestColors.GREEN, TestPage(TestPage(manifest, controlColor = null)).controlColor)
        assertEquals(TestColors.BLUE, TestPage(hasPagesParent, controlColor = null).controlColor)
    }
    // endregion Property: controlColor

    // region Property: multiselectOptionBackgroundColor
    @Test
    fun testPropertyMultiselectOptionBackgroundColor() {
        val page = TestPage(
            parent = Manifest(multiselectOptionBackgroundColor = TestColors.RED),
            multiselectOptionBackgroundColor = TestColors.GREEN,
        )
        assertEquals(TestColors.GREEN, page.multiselectOptionBackgroundColor)
    }

    @Test
    fun testPropertyMultiselectOptionBackgroundColorFallback() {
        val page = TestPage(
            parent = Manifest(multiselectOptionBackgroundColor = TestColors.GREEN),
            multiselectOptionBackgroundColor = null,
        )
        assertEquals(TestColors.GREEN, page.multiselectOptionBackgroundColor)
    }
    // endregion Property: multiselectOptionBackgroundColor

    // region Property: multiselectOptionSelectedColor
    @Test
    fun testPropertyMultiselectOptionSelectedColor() {
        val page = TestPage(
            parent = Manifest(multiselectOptionSelectedColor = TestColors.RED),
            multiselectOptionSelectedColor = TestColors.GREEN,
        )
        assertEquals(TestColors.GREEN, page.multiselectOptionSelectedColor)
    }

    @Test
    fun testPropertyMultiselectOptionSelectedColorFallback() {
        val page = TestPage(
            parent = Manifest(multiselectOptionSelectedColor = TestColors.GREEN),
            multiselectOptionSelectedColor = null,
        )
        assertEquals(TestColors.GREEN, page.multiselectOptionSelectedColor)
    }
    // endregion Property: multiselectOptionSelectedColor

    // region Property: parentPage
    @Test
    fun testParentPage() {
        val manifest = Manifest(
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2", parentPage = "page1")) }
        )

        val page1 = manifest.findPage("page1")!!
        val page2 = manifest.findPage("page2")!!
        assertSame(page1, page2.parentPage)
    }
    // endregion Property: parentPage

    // region Property: nextPage
    @Test
    fun testNextPage() {
        val manifest = Manifest(
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2")) }
        )

        val page1 = manifest.findPage("page1")!!
        val page2 = manifest.findPage("page2")!!
        assertSame(page2, page1.nextPage)
        assertNull(page2.nextPage)
    }
    // endregion Property: nextPage

    // region Property: previousPage
    @Test
    fun testPreviousPage() {
        val manifest = Manifest(
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2")) }
        )

        val page1 = manifest.findPage("page1")!!
        val page2 = manifest.findPage("page2")!!
        assertSame(page1, page2.previousPage)
        assertNull(page1.previousPage)
    }
    // endregion Property: previousPage

    private class TestPage(
        parent: HasPages = Manifest(),
        cardBackgroundColor: PlatformColor? = null,
        controlColor: PlatformColor? = null,
        multiselectOptionBackgroundColor: PlatformColor? = null,
        multiselectOptionSelectedColor: PlatformColor? = null,
        override val analyticsEvents: List<AnalyticsEvent> = emptyList(),
        pages: ((HasPages) -> List<Page>?)? = null,
    ) :
        Page(
            container = parent,
            cardBackgroundColor = cardBackgroundColor,
            controlColor = controlColor,
            multiselectOptionBackgroundColor = multiselectOptionBackgroundColor,
            multiselectOptionSelectedColor = multiselectOptionSelectedColor,
        ),
        HasPages {
        override val pages: List<Page> = pages?.invoke(this).orEmpty()
        override fun <T : Page> supportsPageType(type: KClass<T>) = true
    }
}
