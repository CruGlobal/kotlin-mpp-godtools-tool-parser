package org.cru.godtools.tool.model

import kotlin.native.concurrent.SharedImmutable

internal const val SCHEMA_VERSION = 1

// XML namespaces
internal const val XMLNS_MANIFEST = "https://mobile-content-api.cru.org/xmlns/manifest"
internal const val XMLNS_ARTICLE = "https://mobile-content-api.cru.org/xmlns/article"
internal const val XMLNS_ANALYTICS = "https://mobile-content-api.cru.org/xmlns/analytics"
internal const val XMLNS_CONTENT = "https://mobile-content-api.cru.org/xmlns/content"

// common XML attributes
internal const val XML_PRIMARY_COLOR = "primary-color"
internal const val XML_PRIMARY_TEXT_COLOR = "primary-text-color"
internal const val XML_BACKGROUND_COLOR = "background-color"
internal const val XML_BACKGROUND_IMAGE = "background-image"
internal const val XML_BACKGROUND_IMAGE_GRAVITY = "background-image-align"
internal const val XML_BACKGROUND_IMAGE_SCALE_TYPE = "background-image-scale-type"
internal const val XML_DISMISS_LISTENERS = "dismiss-listeners"
internal const val XML_TEXT_COLOR = "text-color"
internal const val XML_TEXT_SCALE = "text-scale"
internal const val XML_EVENTS = "events"
internal const val XML_LISTENERS = "listeners"

// common colors
@SharedImmutable
internal val TRANSPARENT = color(0, 0, 0, 0.0)
@SharedImmutable
internal val WHITE = color(255, 255, 255, 1.0)

// region Text Sizes
// these text sizes are exposed publicly via relative textScale properties based off the base text size
internal const val TEXT_SIZE_BASE = 16
internal const val TEXT_SIZE_CATEGORY = 30
internal const val TEXT_SIZE_HEADER = 18
internal const val TEXT_SIZE_HEADER_NUMBER = 54
internal const val TEXT_SIZE_HERO_HEADING = 30
internal const val TEXT_SIZE_CARD_LABEL = 18
internal const val TEXT_SIZE_MODAL = 18
internal const val TEXT_SIZE_MODAL_TITLE = 54
// endregion Text Sizes
