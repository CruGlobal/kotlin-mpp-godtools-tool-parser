package org.cru.godtools.shared.tool.state.internal

expect interface Parcelable

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
internal annotation class Parcelize
