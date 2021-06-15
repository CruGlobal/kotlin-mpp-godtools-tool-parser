package org.cru.godtools.tool.service

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.TEST_XML_PULL_PARSER_FACTORY
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
class ManifestParserTest : UsesResources("service") {
    private val parser = ManifestParser(TEST_XML_PULL_PARSER_FACTORY)

    @Test
    fun testParseManifest() {
        val result = assertIs<Result.Data>(parser.parseManifest("manifest_valid.xml"))
        assertEquals(2, result.manifest.tractPages.size)
    }

    @Test
    fun testParseManifestMissingManifest() {
        assertIs<Result.Error.NotFound>(parser.parseManifest("missing.xml"))
    }

    @Test
    fun testParseManifestInvalidManifest() {
        assertIs<Result.Error.Corrupted>(parser.parseManifest("../model/accordion.xml"))
    }
}
