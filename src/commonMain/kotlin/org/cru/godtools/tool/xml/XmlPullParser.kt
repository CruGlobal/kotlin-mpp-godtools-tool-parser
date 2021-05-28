package org.cru.godtools.tool.xml

internal interface XmlPullParser {
    val eventType: Int
    val namespace: String?
    val name: String?

    fun require(type: Int, namespace: String? = null, name: String? = null)

    fun next(): Int
    fun nextTag(): Int
    fun nextText(): String

    fun getAttributeValue(name: String) = getAttributeValue(null, name)
    fun getAttributeValue(namespace: String?, name: String): String?

    companion object {
        const val START_DOCUMENT = 0
        const val END_DOCUMENT = 1
        const val START_TAG = 2
        const val END_TAG = 3
        const val TEXT = 4
    }
}

internal fun XmlPullParser.skipTag() {
    require(XmlPullParser.START_TAG, null, null)

    // loop until we process all nested tags
    var depth = 1
    while (depth > 0) {
        when (next()) {
            XmlPullParser.START_TAG -> depth++
            XmlPullParser.END_TAG -> depth--
        }
    }
}
