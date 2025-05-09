package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.internal.UsesResources

@RunOnAndroidWith(AndroidJUnit4::class)
class CategoryTest : UsesResources() {
    @Test
    fun testParseCategory() = runTest {
        val category = Manifest.parse("categories.xml", ParserConfig()) { getTestXmlParser(it) }.categories.single()
        assertEquals("testParseCategory", category.id)
        assertNotNull(category.banner).also {
            assertEquals("banner.jpg", it.name)
            assertEquals("bannersha1.jpg", it.localName)
        }
        assertEquals(setOf("tag1", "tag2"), category.aemTags)
        assertEquals("Category", category.label!!.text)
        assertEquals(TestColors.RED, category.label!!.textColor)
    }

    @Test
    fun testLabelTextColor() {
        val manifest = Manifest(categoryLabelColor = TestColors.random())
        assertNotNull(Category(manifest, label = { Text(it) }).label) { label ->
            assertEquals(manifest.categoryLabelColor, label.textColor)
            assertNotEquals(manifest.textColor, label.textColor)
        }

        assertNotNull(Category(manifest, label = { Text(it, textColor = TestColors.GREEN) }).label) { label ->
            assertEquals(TestColors.GREEN, label.textColor)
            assertNotEquals(manifest.categoryLabelColor, label.textColor)
            assertNotEquals(manifest.textColor, label.textColor)
        }
    }
}
