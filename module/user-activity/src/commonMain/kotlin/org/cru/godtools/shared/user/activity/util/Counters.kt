package org.cru.godtools.shared.user.activity.util

typealias Counters = Map<String, Int>

internal fun Counters.sum(prefix: String) = entries
    .filter { (counter) -> counter.startsWith(prefix) }
    .sumOf { (_, count) -> count }
