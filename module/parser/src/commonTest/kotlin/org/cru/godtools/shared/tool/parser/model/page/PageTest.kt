package org.cru.godtools.shared.tool.parser.model.page

import com.github.ajalt.colormath.Color
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
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
        Manifest.Type.entries.forEach {
            assertNull(Page.parse(Manifest(type = it), null, getTestXmlParser("page_invalid_type.xml")))
        }
    }

    @Test
    fun testParseInvalidPageNamespace() = runTest {
        // TODO: Switch values() to entries once https://youtrack.jetbrains.com/issue/KT-76027 is released
        Manifest.Type.values().forEach {
            assertNull(Page.parse(Manifest(type = it), null, getTestXmlParser("page_invalid_namespace.xml")))
        }
    }

    @Test
    fun testParseParentPage() = runTest {
        val manifest = Manifest(
            type = Manifest.Type.CYOA,
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2")) }
        )
        assertNotNull(Page.parse(manifest, null, getTestXmlParser("page_content_parent.xml"))) {
            assertSame(manifest.findPage("page1"), it.parentPage)
            assertEquals(mapOf("param" to "value"), it.parentPageParams)
        }
        assertNotNull(Page.parse(manifest, null, getTestXmlParser("page_content_parent_override.xml"))) {
            assertSame(manifest.findPage("page1"), it.parentPage)
            assertEquals(mapOf("param" to "value"), it.parentPageParams)
        }
    }

    @Test
    fun testParseParentPage_pageCollection() = runTest {
        val manifest = Manifest(
            config = ParserConfig().withSupportedFeatures(FEATURE_PAGE_COLLECTION),
            type = Manifest.Type.CYOA,
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2")) },
        )
        assertNotNull(Page.parse(manifest, null, getTestXmlParser("page_content_parent.xml"))) {
            assertSame(manifest.findPage("page1"), it.parentPage)
            assertEquals(mapOf("param" to "value"), it.parentPageParams)
        }
        assertNotNull(Page.parse(manifest, null, getTestXmlParser("page_content_parent_override.xml"))) {
            assertSame(manifest.findPage("page2"), it.parentPage)
            assertEquals(mapOf("param" to "value2"), it.parentPageParams)
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
        val manifest = Manifest(cardBackgroundColor = TestColors.random())
        val hasPagesParent = TestPage(parent = manifest, cardBackgroundColor = TestColors.random())

        assertEquals(TestColors.RED, TestPage(manifest, cardBackgroundColor = TestColors.RED).cardBackgroundColor)
        assertEquals(TestColors.RED, TestPage(hasPagesParent, cardBackgroundColor = TestColors.RED).cardBackgroundColor)
        assertEquals(manifest.cardBackgroundColor, TestPage(manifest, cardBackgroundColor = null).cardBackgroundColor)
        assertEquals(
            manifest.cardBackgroundColor,
            TestPage(TestPage(manifest, cardBackgroundColor = null)).cardBackgroundColor,
        )
        assertEquals(
            hasPagesParent.cardBackgroundColor,
            TestPage(hasPagesParent, cardBackgroundColor = null).cardBackgroundColor,
        )
    }
    // endregion Property: cardBackgroundColor

    // region Property: controlColor
    @Test
    fun testPropertyControlColor() {
        val manifest = Manifest(pageControlColor = TestColors.random())
        val hasPagesParent = TestPage(parent = manifest, controlColor = TestColors.random())

        assertEquals(TestColors.RED, TestPage(manifest, controlColor = TestColors.RED).controlColor)
        assertEquals(TestColors.RED, TestPage(hasPagesParent, controlColor = TestColors.RED).controlColor)
        assertEquals(manifest.pageControlColor, TestPage(manifest, controlColor = null).controlColor)
        assertEquals(manifest.pageControlColor, TestPage(TestPage(manifest, controlColor = null)).controlColor)
        assertEquals(hasPagesParent.controlColor, TestPage(hasPagesParent, controlColor = null).controlColor)
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
    fun testParentPage_manifest() {
        val manifest = Manifest(
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2", parentPage = "page1")) }
        )

        val page1 = manifest.findPage("page1")!!
        val page2 = manifest.findPage("page2")!!
        assertSame(page1, page2.parentPage)
    }

    @Test
    fun testParentPage_hasPagesParent() {
        val parent = TestPage(
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2", parentPage = "page1")) }
        )

        val page1 = parent.findPage("page1")!!
        val page2 = parent.findPage("page2")!!
        assertSame(page1, page2.parentPage)
    }

    @Test
    fun testParentPage_restrictToCurrentContainer() {
        val manifest = Manifest(
            pages = {
                listOf(
                    TestPage(it, id = "page1") {
                        listOf(
                            TestPage(it, id = "page1_1"),

                            TestPage(it, id = "test1", parentPage = "page1_1"),
                            TestPage(it, id = "test2", parentPage = "page2"),
                        )
                    },
                    TestPage(it, id = "page2"),

                    // parentPage tests
                    TestPage(it, id = "test3", parentPage = "page1"),
                    TestPage(it, id = "test4", parentPage = "page1_1"),
                )
            },
        )
        val page1 = manifest.findPage("page1") as TestPage
        val page11 = page1.findPage("page1_1")!!

        assertSame(page11, page1.findPage("test1")!!.parentPage)
        assertNull(page1.findPage("test2")!!.parentPage, "page2 is not in the same page container as test2")
        assertSame(page1, manifest.findPage("test3")!!.parentPage)
        assertNull(manifest.findPage("test4")!!.parentPage, "page1_1 is not in the same page container as test4")
    }

    @Test
    fun testParentPage_hasParams() {
        val manifest = Manifest(
            pages = {
                listOf(
                    TestPage(it, id = "page1"),

                    // parentPage tests
                    TestPage(it, id = "test1", parentPage = "page1"),
                    TestPage(it, id = "test2", parentPage = "page1?active-page=page1_2"),
                    TestPage(it, id = "test3", parentPage = "page1?active-page=page2"),
                    TestPage(it, id = "test4", parentPage = "missing?active-page=page2"),
                )
            },
        )
        val page1 = manifest.findPage("page1")!!

        assertSame(page1, manifest.findPage("test1")!!.parentPage)
        assertSame(page1, manifest.findPage("test2")!!.parentPage)
        assertSame(page1, manifest.findPage("test3")!!.parentPage)
        assertNull(manifest.findPage("test4")!!.parentPage, "missing doesn't exist")
    }
    // endregion Property: parentPage

    // region Property: parentPageParams
    @Test
    fun testParentPageParams() {
        val manifest = Manifest(
            pages = {
                listOf(
                    TestPage(it, id = "page1"),

                    TestPage(it, id = "test1", parentPage = "page1"),
                    TestPage(it, id = "test2", parentPage = "page1?active-page=page1_2"),
                    TestPage(it, id = "test3", parentPage = "page1?a=1&c=3&b=2"),
                    TestPage(it, id = "test4", parentPage = null),
                    TestPage(it, id = "test5", parentPage = "missing?active-page=page2"),
                )
            },
        )

        assertEquals(emptyMap(), manifest.findPage("test1")!!.parentPageParams)
        assertEquals(mapOf("active-page" to "page1_2"), manifest.findPage("test2")!!.parentPageParams)
        assertEquals(mapOf("a" to "1", "b" to "2", "c" to "3"), manifest.findPage("test3")!!.parentPageParams)
        assertEquals(emptyMap(), manifest.findPage("test4")!!.parentPageParams)
        assertEquals(
            emptyMap(),
            manifest.findPage("test5")!!.parentPageParams,
            "Don't parse parameters if the parentPage doesn't exist",
        )
    }
    // endregion Property: parentPageParams

    // region Property: nextPage
    @Test
    fun testNextPage_manifest() {
        val manifest = Manifest(
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2")) }
        )

        val page1 = manifest.findPage("page1")!!
        val page2 = manifest.findPage("page2")!!
        assertSame(page2, page1.nextPage)
        assertNull(page2.nextPage)
    }

    @Test
    fun testNextPage_hasPagesParent() {
        val parent = TestPage(
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2")) }
        )

        val page1 = parent.findPage("page1")!!
        val page2 = parent.findPage("page2")!!
        assertSame(page2, page1.nextPage)
        assertNull(page2.nextPage)
    }
    // endregion Property: nextPage

    // region Property: previousPage
    @Test
    fun testPreviousPage_manifest() {
        val manifest = Manifest(
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2")) }
        )

        val page1 = manifest.findPage("page1")!!
        val page2 = manifest.findPage("page2")!!
        assertSame(page1, page2.previousPage)
        assertNull(page1.previousPage)
    }

    @Test
    fun testPreviousPage_hasPagesParent() {
        val parent = TestPage(
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2")) }
        )

        val page1 = parent.findPage("page1")!!
        val page2 = parent.findPage("page2")!!
        assertSame(page1, page2.previousPage)
        assertNull(page1.previousPage)
    }
    // endregion Property: previousPage

    class TestPage(
        parent: HasPages = Manifest(),
        id: String? = null,
        parentPage: String? = null,
        cardBackgroundColor: Color? = null,
        controlColor: Color? = null,
        multiselectOptionBackgroundColor: Color? = null,
        multiselectOptionSelectedColor: Color? = null,
        override val analyticsEvents: List<AnalyticsEvent> = emptyList(),
        pages: ((HasPages) -> List<Page>) = { listOf() },
    ) :
        Page(
            container = parent,
            id = id,
            parentPage = parentPage,
            cardBackgroundColor = cardBackgroundColor,
            controlColor = controlColor,
            multiselectOptionBackgroundColor = multiselectOptionBackgroundColor,
            multiselectOptionSelectedColor = multiselectOptionSelectedColor,
        ),
        HasPages {
        override val pages: List<Page> = pages(this)
        override fun <T : Page> supportsPageType(type: KClass<T>) = true
    }
}
