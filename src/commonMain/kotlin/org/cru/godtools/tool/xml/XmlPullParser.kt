package org.cru.godtools.tool.xml

internal interface XmlPullParser {
    fun require(type: Int, namespace: String?, name: String?)

    fun next(): Int
    fun nextTag(): Int

    fun getAttributeValue(namespace: String?, name: String): String?

    companion object {
        const val START_DOCUMENT = 0
        const val END_DOCUMENT = 1
        const val START_TAG = 2
        const val END_TAG = 3
        const val TEXT = 4
    }
}
