package org.cru.godtools.shared.tool.parser.model.page

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_PAGE_COLLECTION
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserException

@RunOnAndroidWith(AndroidJUnit4::class)
class PageCollectionPageTest : UsesResources("model/page") {
    private val manifest = Manifest(
        config = ParserConfig().withSupportedFeatures(FEATURE_PAGE_COLLECTION),
        type = Manifest.Type.CYOA,
        pageXmlFiles = listOf(
            Manifest.XmlFile("ref_page_valid", "page_content.xml"),
            Manifest.XmlFile("ref_page_invalid", "page_page-collection_imports.xml"),
        ),
    )

    // region Parse XML
    @Test
    fun testParsePageCollectionPage() = runTest {
        assertNotNull(
            PageCollectionPage.parse(manifest, null, getTestXmlParser("page_page-collection.xml"), parseFile)
        ) {
            assertEquals(1, it.analyticsEvents.size)
            assertEquals(1, it.pages.size)
            assertNotNull(it.pages[0]) { page ->
                assertIs<ContentPage>(page)
                assertEquals("content_page", page.id)
            }
        }
    }

    @Test
    fun testParsePageCollectionPage_PageImports() = runTest {
        assertNotNull(
            PageCollectionPage.parse(manifest, null, getTestXmlParser("page_page-collection_imports.xml"), parseFile)
        ) {
            assertEquals(1, it.analyticsEvents.size)
            assertEquals(2, it.pages.size)
            assertEquals("embedded_content_page", it.pages[0].id)
            assertEquals("content_page", it.pages[1].id)
        }
    }

    @Test
    fun testParsePageCollectionPageInvalidType() = runTest {
        assertFailsWith(XmlPullParserException::class) {
            PageCollectionPage.parse(manifest, null, getTestXmlParser("page_invalid_type.xml"), parseFile)
        }
    }
    // endregion Parse XML
}
