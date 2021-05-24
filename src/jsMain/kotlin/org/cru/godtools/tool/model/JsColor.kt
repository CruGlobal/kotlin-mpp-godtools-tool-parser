package org.cru.godtools.tool.model

actual typealias Color = String

internal actual inline fun color(red: Int, green: Int, blue: Int, alpha: Double) =
    "rgba($red,$green,$blue,$alpha)"
