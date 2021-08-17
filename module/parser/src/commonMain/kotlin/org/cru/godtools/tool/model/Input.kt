package org.cru.godtools.tool.model

import org.cru.godtools.tool.model.Input.Type.Companion.toTypeOrNull
import org.cru.godtools.tool.xml.XmlPullParser
import org.cru.godtools.tool.xml.parseChildren
import kotlin.native.concurrent.SharedImmutable

private const val XML_TYPE = "type"
private const val XML_TYPE_TEXT = "text"
private const val XML_TYPE_EMAIL = "email"
private const val XML_TYPE_PHONE = "phone"
private const val XML_TYPE_HIDDEN = "hidden"
private const val XML_NAME = "name"
private const val XML_REQUIRED = "required"
private const val XML_VALUE = "value"
private const val XML_LABEL = "label"
private const val XML_PLACEHOLDER = "placeholder"

@SharedImmutable
private val REGEX_VALIDATE_EMAIL = Regex(".+@.+")

class Input : Content {
    internal companion object {
        internal const val XML_INPUT = "input"
    }

    val type: Type
    val name: String?
    val value: String?
    val isRequired: Boolean
    val label: Text?
    val placeholder: Text?

    internal constructor(parent: Base, parser: XmlPullParser) : super(parent, parser) {
        parser.require(XmlPullParser.START_TAG, XMLNS_CONTENT, XML_INPUT)

        type = parser.getAttributeValue(XML_TYPE)?.toTypeOrNull() ?: Type.DEFAULT
        name = parser.getAttributeValue(XML_NAME)
        value = parser.getAttributeValue(XML_VALUE)
        isRequired = parser.getAttributeValue(XML_REQUIRED)?.toBoolean() ?: false

        // process any child elements
        var label: Text? = null
        var placeholder: Text? = null
        parser.parseChildren {
            when (parser.namespace) {
                XMLNS_CONTENT -> when (parser.name) {
                    XML_LABEL -> label = parser.parseTextChild(this, XMLNS_CONTENT, XML_LABEL)
                    XML_PLACEHOLDER -> placeholder = parser.parseTextChild(this, XMLNS_CONTENT, XML_PLACEHOLDER)
                }
            }
        }
        this.label = label
        this.placeholder = placeholder
    }

    fun validateValue(raw: String?) = when {
        isRequired && raw.isNullOrBlank() -> Error.Required
        type == Type.EMAIL && !raw.isNullOrBlank() && !REGEX_VALIDATE_EMAIL.matches(raw) -> Error.InvalidEmail
        else -> null
    }

    sealed class Error {
        object Required : Error()
        object InvalidEmail : Error()
    }

    enum class Type {
        TEXT, EMAIL, PHONE, HIDDEN;

        internal companion object {
            internal val DEFAULT = TEXT

            internal fun String.toTypeOrNull() = when (this) {
                XML_TYPE_EMAIL -> EMAIL
                XML_TYPE_HIDDEN -> HIDDEN
                XML_TYPE_PHONE -> PHONE
                XML_TYPE_TEXT -> TEXT
                else -> null
            }
        }
    }
}

val Input?.type get() = this?.type ?: Input.Type.DEFAULT
