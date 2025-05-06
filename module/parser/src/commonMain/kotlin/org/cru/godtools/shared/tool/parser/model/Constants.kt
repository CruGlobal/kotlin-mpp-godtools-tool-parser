package org.cru.godtools.shared.tool.parser.model

import org.cru.godtools.shared.tool.parser.internal.color

internal const val SCHEMA_VERSION = 2

// XML namespaces
internal const val XMLNS_MANIFEST = "https://mobile-content-api.cru.org/xmlns/manifest"
internal const val XMLNS_ARTICLE = "https://mobile-content-api.cru.org/xmlns/article"
internal const val XMLNS_ANALYTICS = "https://mobile-content-api.cru.org/xmlns/analytics"
internal const val XMLNS_CONTENT = "https://mobile-content-api.cru.org/xmlns/content"
internal const val XMLNS_CYOA = "https://mobile-content-api.cru.org/xmlns/cyoa"
internal const val XMLNS_WEB = "https://mobile-content-api.cru.org/xmlns/web"

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
internal const val XML_LISTENERS = "listeners"

// common colors
internal val TRANSPARENT = color(0, 0, 0, 0.0)
internal val WHITE = color(255, 255, 255, 1.0)
