package org.cru.godtools.shared.tool.parser

import io.github.aakira.napier.Napier
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.CapturingAntilog
import org.cru.godtools.shared.tool.parser.internal.TEST_XML_PULL_PARSER_FACTORY
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.xml.XmlPullParser
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserFactory

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ManifestParserTest : UsesResources(null) {
    private val logger = CapturingAntilog()
    private val parser = ManifestParser(TEST_XML_PULL_PARSER_FACTORY, ParserConfig())

    @BeforeTest
    fun setup() {
        Napier.base(logger)
    }

    @Test
    fun testParseManifest() = runTest {
        val result = assertIs<ParserResult.Data>(parser.parseManifest("manifest_valid.xml"))
        assertEquals(2, result.manifest.pages.size)
    }

    @Test
    fun testParseManifestMissingManifest() = runTest {
        assertIs<ParserResult.Error.NotFound>(parser.parseManifest("missing.xml"))
    }

    @Test
    fun testParseManifestInvalidManifest() = runTest {
        assertIs<ParserResult.Error.Corrupted>(parser.parseManifest("model/accordion.xml"))
    }

    @Test
    fun testParseManifestMissingPage() = runTest {
        assertIs<ParserResult.Error.NotFound>(parser.parseManifest("manifest_missing_page.xml"))
    }

    @Test
    fun testParseManifestCancellationException() = runTest {
        val parser = ManifestParser(
            object : XmlPullParserFactory() {
                override suspend fun getXmlParser(fileName: String): XmlPullParser? {
                    Semaphore(1, 1).acquire()
                    return null
                }
            },
            ParserConfig()
        )
        val task = launch { parser.parseManifest("") }
        runCurrent()
        task.cancelAndJoin()
        assertTrue(task.isCompleted)
        assertTrue(task.isCancelled)
        assertTrue(logger.logsSent.isEmpty())
    }

    @Test
    fun testParseManifestUnrecognizedException() = runTest {
        val exception = Exception()
        val parser = ManifestParser(
            object : XmlPullParserFactory() {
                override suspend fun getXmlParser(fileName: String) = throw exception
            },
            ParserConfig()
        )
        val response = assertIs<ParserResult.Error>(parser.parseManifest(""))
        assertEquals(ParserResult.Error::class, response::class)
        assertSame(exception, response.error)
        assertSame(exception, logger.logsSent.first().throwable)
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
        ManifestParser(PrefixXmlPullParserFactory(prefix, TEST_XML_PULL_PARSER_FACTORY), ParserConfig())
            .parseManifest(manifest)

    internal class PrefixXmlPullParserFactory(
        private val prefix: String,
        private val delegate: XmlPullParserFactory
    ) : XmlPullParserFactory() {
        override suspend fun getXmlParser(fileName: String) = delegate.getXmlParser("$prefix$fileName")
    }
    // endregion Tool Parsing Tests
}
