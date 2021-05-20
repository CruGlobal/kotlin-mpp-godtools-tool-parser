package org.cru.godtools.tool.xml

internal class AndroidXmlPullParser(delegate: org.xmlpull.v1.XmlPullParser) :
    XmlPullParser, org.xmlpull.v1.XmlPullParser by delegate
