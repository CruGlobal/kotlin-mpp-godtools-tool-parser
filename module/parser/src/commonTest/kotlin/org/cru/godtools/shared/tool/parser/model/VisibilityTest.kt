package org.cru.godtools.shared.tool.parser.model

import app.cash.turbine.Event
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.shared.tool.parser.expressions.Expression
import org.cru.godtools.shared.tool.parser.expressions.toExpressionOrNull
import org.cru.godtools.shared.tool.state.State
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class VisibilityTest {
    private class VisibilityObject(
        override val invisibleIf: Expression? = null,
        override val goneIf: Expression? = null,
    ) : Visibility

    private val state = State()

    @Test
    fun verifyIsGone() {
        with(VisibilityObject(goneIf = "isSet(a)".toExpressionOrNull())) {
            assertFalse(isGone(state))
            state.setVar("a", listOf("test"))
            assertTrue(isGone(state))
        }
    }

    @Test
    fun verifyIsGoneFlow() = runTest {
        VisibilityObject(goneIf = "isSet(a) || isSet(b)".toExpressionOrNull()).isGoneFlow(state).test {
            assertFalse(awaitItem(), "Initially not hidden")

            state.setVar("c", listOf("test"))
            expectNoEvents() // 'c' should have no effect on isHidden result

            state.setVar("a", listOf("test"))
            assertTrue(awaitItem(), "'a' is now set")

            state.setVar("a", emptyList())
            assertFalse(awaitItem(), "'a' is no longer set")

            state.setVar("b", listOf("test"))
            assertTrue(awaitItem(), "'b' is now set")

            state.setVar("a", listOf("test"))
            expectNoEvents() // 'a' is now set, but shouldn't change isHidden result

            state.setVar("b", emptyList())
            expectNoEvents() // 'a' is still set, so isHidden result shouldn't change

            state.setVar("a", emptyList())
            assertFalse(awaitItem(), "'a' is no longer set")
        }
    }

    @Test
    fun verifyIsGoneDefault() = runTest {
        val content = VisibilityObject()
        assertFalse(content.isGone(state))
        content.isGoneFlow(state).test {
            assertFalse(awaitItem(), "Initially not hidden")

            for (i in 1..10) {
                state.setVar("a$i", listOf("test"))
                assertFalse(content.isGone(state))
            }

            // there should have been no more items emitted. the flow may complete, but doesn't have to
            assertTrue(cancelAndConsumeRemainingEvents().filterNot { it is Event.Complete }.isEmpty())
        }
    }

    @Test
    fun verifyIsInvisible() {
        with(VisibilityObject(invisibleIf = "isSet(a)".toExpressionOrNull())) {
            assertFalse(isInvisible(state))
            state.setVar("a", listOf("test"))
            assertTrue(isInvisible(state))
        }
    }

    @Test
    fun verifyIsInvisibleFlow() = runTest {
        val content = VisibilityObject(invisibleIf = "isSet(a) || isSet(b)".toExpressionOrNull())
        content.isInvisibleFlow(state).test {
            assertFalse(awaitItem(), "Initially not invisible")

            state.setVar("c", listOf("test"))
            expectNoEvents() // 'c' should have no effect on isInvisible result

            state.setVar("a", listOf("test"))
            assertTrue(awaitItem(), "'a' is now set")

            state.setVar("a", emptyList())
            assertFalse(awaitItem(), "'a' is no longer set")

            state.setVar("b", listOf("test"))
            assertTrue(awaitItem(), "'b' is now set")

            state.setVar("a", listOf("test"))
            expectNoEvents() // 'a' is now set, but shouldn't change isInvisible result

            state.setVar("b", emptyList())
            expectNoEvents() // 'a' is still set, so isInvisible result shouldn't change

            state.setVar("a", emptyList())
            assertFalse(awaitItem(), "'a' is no longer set")
        }
    }

    @Test
    fun verifyIsInvisibleDefault() = runTest {
        val content = VisibilityObject()
        assertFalse(content.isInvisible(state))
        content.isInvisibleFlow(state).test {
            assertFalse(awaitItem(), "Initially not invisible")

            for (i in 1..10) {
                state.setVar("a$i", listOf("test"))
                assertFalse(content.isGone(state))
            }

            // there should have been no more items emitted. the flow may complete, but doesn't have to
            assertTrue(cancelAndConsumeRemainingEvents().filterNot { it is Event.Complete }.isEmpty())
        }
    }
}
