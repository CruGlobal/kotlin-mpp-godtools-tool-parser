package org.cru.godtools.tool.service

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.TEST_XML_PULL_PARSER_FACTORY
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.xml.XmlPullParserFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ManifestParserTest : UsesResources("service") {
    private val parser = ManifestParser(TEST_XML_PULL_PARSER_FACTORY)

    @Test
    fun testParseManifest() = runTest {
        val result = assertIs<ParserResult.Data>(parser.parseManifest("manifest_valid.xml"))
        assertEquals(2, result.manifest.tractPages.size)
    }

    @Test
    fun testParseManifestMissingManifest() = runTest {
        assertIs<ParserResult.Error.NotFound>(parser.parseManifest("missing.xml"))
    }

    @Test
    fun testParseManifestInvalidManifest() = runTest {
        assertIs<ParserResult.Error.Corrupted>(parser.parseManifest("../model/accordion.xml"))
    }

    @Test
    fun testParseManifestMissingPage() = runTest {
        assertIs<ParserResult.Error.NotFound>(parser.parseManifest("manifest_missing_page.xml"))
    }

    @Test
    fun testParseManifestInvalidPage() = runTest {
        assertIs<ParserResult.Error.Corrupted>(parser.parseManifest("manifest_invalid_page.xml"))
    }

    // region Tool Parsing Tests
    @Test
    fun testParseManifestTheFourKo() = runTest {
        assertIs<ParserResult.Data>(
            parseTool(
                "tools/thefour/ko/",
                "c031c615dc52f6ebbb96076bef08351c5dab7edfd5f2a221905af707221cbdd0.xml"
            )
        )
    }

    private suspend fun parseTool(prefix: String, manifest: String) =
        ManifestParser(PrefixXmlPullParserFactory(prefix, TEST_XML_PULL_PARSER_FACTORY)).parseManifest(manifest)

    internal class PrefixXmlPullParserFactory(
        private val prefix: String,
        private val delegate: XmlPullParserFactory
    ) : XmlPullParserFactory() {
        override suspend fun getXmlParser(fileName: String) = delegate.getXmlParser("$prefix$fileName")
    }
    // endregion Tool Parsing Tests
}
