package org.cru.godtools.tool.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.cru.godtools.tool.xml.AndroidXmlPullParserFactory
import org.cru.godtools.tool.xml.XmlPullParserFactory

internal actual val UsesResources.TEST_XML_PULL_PARSER_FACTORY: XmlPullParserFactory
    get() = object : AndroidXmlPullParserFactory() {
        override suspend fun openFile(fileName: String) = withContext(Dispatchers.IO) {
            this@TEST_XML_PULL_PARSER_FACTORY::class.java.getResourceAsStream(fileName)
        }
    }

// region Android Robolectric
actual typealias RunOnAndroidWith = org.junit.runner.RunWith
actual typealias Runner = org.junit.runner.Runner
actual typealias AndroidJUnit4 = AndroidJUnit4
// endregion Android Robolectric

// region Kotlin Coroutines
actual fun runBlockingTest(block: suspend CoroutineScope.() -> Unit) = runBlocking { block() }
// endregion Kotlin Coroutines
