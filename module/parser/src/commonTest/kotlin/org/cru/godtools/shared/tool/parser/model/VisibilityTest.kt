package org.cru.godtools.shared.tool.parser.model

import app.cash.turbine.Event
import app.cash.turbine.test
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.cru.godtools.shared.tool.parser.expressions.Expression
import org.cru.godtools.shared.tool.parser.expressions.SimpleExpressionContext
import org.cru.godtools.shared.tool.parser.expressions.toExpressionOrNull

@OptIn(ExperimentalCoroutinesApi::class)
class VisibilityTest {
    private class VisibilityObject(
        override val invisibleIf: Expression? = null,
        override val goneIf: Expression? = null,
    ) : Visibility

    private val expressionCtx = SimpleExpressionContext()

    @AfterTest
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun verifyIsGone() {
        with(VisibilityObject(goneIf = "isSet(a)".toExpressionOrNull())) {
            assertFalse(isGone(expressionCtx))
            expressionCtx.setVar("a", listOf("test"))
            assertTrue(isGone(expressionCtx))
        }
    }

    @Test
    fun verifyIsGoneFlow() = runTest {
        VisibilityObject(goneIf = "isSet(a) || isSet(b)".toExpressionOrNull()).isGoneFlow(expressionCtx).test {
            assertFalse(awaitItem(), "Initially not hidden")

            expressionCtx.setVar("c", listOf("test"))
            expectNoEvents() // 'c' should have no effect on isHidden result

            expressionCtx.setVar("a", listOf("test"))
            assertTrue(awaitItem(), "'a' is now set")

            expressionCtx.setVar("a", emptyList())
            assertFalse(awaitItem(), "'a' is no longer set")

            expressionCtx.setVar("b", listOf("test"))
            assertTrue(awaitItem(), "'b' is now set")

            expressionCtx.setVar("a", listOf("test"))
            expectNoEvents() // 'a' is now set, but shouldn't change isHidden result

            expressionCtx.setVar("b", emptyList())
            expectNoEvents() // 'a' is still set, so isHidden result shouldn't change

            expressionCtx.setVar("a", emptyList())
            assertFalse(awaitItem(), "'a' is no longer set")
        }
    }

    @Test
    fun verifyWatchIsGone() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        var isGone = true
        val watcher = VisibilityObject(goneIf = "isSet(a) || isSet(b)".toExpressionOrNull())
            .watchIsGone(expressionCtx) { isGone = it }

        assertFalse(isGone, "Initially not hidden")

        expressionCtx.setVar("c", listOf("test"))
        assertFalse(isGone, "'c' should have no effect on isHidden result")

        expressionCtx.setVar("a", listOf("test"))
        assertTrue(isGone, "'a' is now set")

        expressionCtx.setVar("a", emptyList())
        assertFalse(isGone, "'a' is no longer set")

        expressionCtx.setVar("b", listOf("test"))
        assertTrue(isGone, "'b' is now set")

        expressionCtx.setVar("a", listOf("test"))
        assertTrue(isGone, "'a' is now set, but shouldn't change isHidden result")

        expressionCtx.setVar("b", emptyList())
        assertTrue(isGone, "'a' is still set, so isHidden result shouldn't change")

        expressionCtx.setVar("a", emptyList())
        assertFalse(isGone, "'a' is no longer set")

        watcher.close()
    }

    @Test
    fun verifyIsGoneDefault() = runTest {
        val content = VisibilityObject()
        assertFalse(content.isGone(expressionCtx))
        content.isGoneFlow(expressionCtx).test {
            assertFalse(awaitItem(), "Initially not hidden")

            for (i in 1..10) {
                expressionCtx.setVar("a$i", listOf("test"))
                assertFalse(content.isGone(expressionCtx))
            }

            // there should have been no more items emitted. the flow may complete, but doesn't have to
            assertTrue(cancelAndConsumeRemainingEvents().filterNot { it is Event.Complete }.isEmpty())
        }
    }

    @Test
    fun verifyIsInvisible() {
        with(VisibilityObject(invisibleIf = "isSet(a)".toExpressionOrNull())) {
            assertFalse(isInvisible(expressionCtx))
            expressionCtx.setVar("a", listOf("test"))
            assertTrue(isInvisible(expressionCtx))
        }
    }

    @Test
    fun verifyIsInvisibleFlow() = runTest {
        val content = VisibilityObject(invisibleIf = "isSet(a) || isSet(b)".toExpressionOrNull())
        content.isInvisibleFlow(expressionCtx).test {
            assertFalse(awaitItem(), "Initially not invisible")

            expressionCtx.setVar("c", listOf("test"))
            expectNoEvents() // 'c' should have no effect on isInvisible result

            expressionCtx.setVar("a", listOf("test"))
            assertTrue(awaitItem(), "'a' is now set")

            expressionCtx.setVar("a", emptyList())
            assertFalse(awaitItem(), "'a' is no longer set")

            expressionCtx.setVar("b", listOf("test"))
            assertTrue(awaitItem(), "'b' is now set")

            expressionCtx.setVar("a", listOf("test"))
            expectNoEvents() // 'a' is now set, but shouldn't change isInvisible result

            expressionCtx.setVar("b", emptyList())
            expectNoEvents() // 'a' is still set, so isInvisible result shouldn't change

            expressionCtx.setVar("a", emptyList())
            assertFalse(awaitItem(), "'a' is no longer set")
        }
    }

    @Test
    fun verifyWatchIsInvisible() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        var isInvisible = true
        val watcher = VisibilityObject(invisibleIf = "isSet(a) || isSet(b)".toExpressionOrNull())
            .watchIsInvisible(expressionCtx) { isInvisible = it }

        assertFalse(isInvisible, "Initially not hidden")

        expressionCtx.setVar("c", listOf("test"))
        assertFalse(isInvisible, "'c' should have no effect on isInvisible result")

        expressionCtx.setVar("a", listOf("test"))
        assertTrue(isInvisible, "'a' is now set")

        expressionCtx.setVar("a", emptyList())
        assertFalse(isInvisible, "'a' is no longer set")

        expressionCtx.setVar("b", listOf("test"))
        assertTrue(isInvisible, "'b' is now set")

        expressionCtx.setVar("a", listOf("test"))
        assertTrue(isInvisible, "'a' is now set, but shouldn't change isInvisible result")

        expressionCtx.setVar("b", emptyList())
        assertTrue(isInvisible, "'a' is still set, so isInvisible result shouldn't change")

        expressionCtx.setVar("a", emptyList())
        assertFalse(isInvisible, "'a' is no longer set")

        watcher.close()
    }

    @Test
    fun verifyIsInvisibleDefault() = runTest {
        val content = VisibilityObject()
        assertFalse(content.isInvisible(expressionCtx))
        content.isInvisibleFlow(expressionCtx).test {
            assertFalse(awaitItem(), "Initially not invisible")

            for (i in 1..10) {
                expressionCtx.setVar("a$i", listOf("test"))
                assertFalse(content.isGone(expressionCtx))
            }

            // there should have been no more items emitted. the flow may complete, but doesn't have to
            assertTrue(cancelAndConsumeRemainingEvents().filterNot { it is Event.Complete }.isEmpty())
        }
    }

    @Test
    fun verifyWatchVisibility() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        var isGone = true
        var isInvisible = true
        val watcher = VisibilityObject(
            goneIf = "isSet(a)".toExpressionOrNull(),
            invisibleIf = "isSet(b)".toExpressionOrNull(),
        ).watchVisibility(expressionCtx) { invisible, gone ->
            isGone = gone
            isInvisible = invisible
        }

        assertFalse(isGone, "Initially not hidden")
        assertFalse(isInvisible, "Initially not hidden")

        expressionCtx.setVar("c", listOf("test"))
        assertFalse(isGone, "'c' should have no effect on isGone result")
        assertFalse(isInvisible, "'c' should have no effect on isInvisible result")

        expressionCtx.setVar("a", listOf("test"))
        assertTrue(isGone, "'a' is now set")
        assertFalse(isInvisible, "'a' should have no effect on isInvisible result")

        expressionCtx.setVar("a", emptyList())
        assertFalse(isGone, "'a' is no longer set")
        assertFalse(isInvisible, "'a' should have no effect on isInvisible result")

        expressionCtx.setVar("b", listOf("test"))
        assertFalse(isGone, "'a' should have no effect on isGone result")
        assertTrue(isInvisible, "'b' is now set")

        expressionCtx.setVar("a", listOf("test"))
        assertTrue(isGone, "'a' is now set")
        assertTrue(isInvisible, "'a' is now set, but shouldn't change isInvisible result")

        expressionCtx.setVar("b", emptyList())
        assertTrue(isGone, "'a' is still set, so isGone result shouldn't change")
        assertFalse(isInvisible, "'b' is no longer set")

        expressionCtx.setVar("a", emptyList())
        assertFalse(isGone, "'a' is no longer set")
        assertFalse(isInvisible, "'a' should have no effect on isInvisible result")

        watcher.close()
    }
}
