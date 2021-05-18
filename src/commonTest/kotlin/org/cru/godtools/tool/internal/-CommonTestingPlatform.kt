package org.cru.godtools.tool.internal

import org.cru.godtools.tool.xml.XmlPullParserFactory
import kotlin.reflect.KClass

expect val Any.TEST_XML_PULL_PARSER_FACTORY: XmlPullParserFactory

// region Android Robolectric
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class RunOnAndroidWith(val value: KClass<out Runner>)
expect abstract class Runner
expect class AndroidJUnit4 : Runner
// endregion Android Robolectric
