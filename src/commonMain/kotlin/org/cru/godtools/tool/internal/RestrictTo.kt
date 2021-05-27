package org.cru.godtools.tool.internal

// HACK: create a local RestrictTo annotation because we can use expect/actual with the AndroidX RestrictTo annotation.
//       see: https://youtrack.jetbrains.com/issue/KT-20900
internal annotation class RestrictTo(vararg val value: Scope) {
    enum class Scope {
        /**
         * Restrict usage to code within the same library (e.g. the same
         * gradle group ID and artifact ID).
         */
        LIBRARY,

        /**
         * Restrict usage to tests.
         */
        TESTS,

        /**
         * Restrict usage to subclasses of the enclosing class.
         *
         *
         * **Note:** This scope should not be used to annotate
         * packages.
         */
        SUBCLASSES
    }
}
