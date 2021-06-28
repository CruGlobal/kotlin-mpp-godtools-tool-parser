package org.cru.godtools.tool.internal

import kotlinx.coroutines.CoroutineScope
import org.cru.godtools.tool.xml.XmlPullParserFactory
import kotlin.reflect.KClass

abstract class UsesResources(internal val resourcesDir: String = "model") {
    internal suspend fun getTestXmlParser(name: String) =
        TEST_XML_PULL_PARSER_FACTORY.getXmlParser(name)!!.apply { nextTag() }
}
internal expect val UsesResources.TEST_XML_PULL_PARSER_FACTORY: XmlPullParserFactory

// region Android Robolectric
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class RunOnAndroidWith(val value: KClass<out Runner>)
expect abstract class Runner
expect class AndroidJUnit4 : Runner
// endregion Android Robolectric

// region Kotlin Coroutines
// Copied w/ modifications from: https://github.com/Kotlin/kotlinx.coroutines/issues/1996#issuecomment-728562784
expect fun runBlockingTest(block: suspend CoroutineScope.() -> Unit)
// endregion Kotlin Coroutines
