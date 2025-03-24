package org.cru.godtools.shared.tool.parser.xml

internal class AndroidXmlPullParser(private val delegate: org.xmlpull.v1.XmlPullParser) : XmlPullParser {
    override val eventType get() = delegate.eventType
    override val namespace: String? get() = delegate.namespace
    override val name: String? get() = delegate.name

    override fun require(type: Int, namespace: String?, name: String?) = delegate.require(type, namespace, name)

    override fun next() = delegate.next()
    override fun nextTag() = delegate.nextTag()
    override fun nextText(): String = delegate.nextText()

    override fun getAttributeValue(name: String): String? = delegate.getAttributeValue("", name)
    override fun getAttributeValue(namespace: String?, name: String): String? =
        delegate.getAttributeValue(namespace.orEmpty(), name)
}
