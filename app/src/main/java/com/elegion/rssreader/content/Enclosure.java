package com.elegion.rssreader.content;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by Andrey Kulikov on 20.09.14.
 */
@Root(name = "enclosure", strict = false)
public class Enclosure {

    @Attribute(name = "url")
    private String mUrl;

    @Attribute(name = "type")
    private String mType;

    public String getUrl() {
        return mUrl;
    }

    public String getType() {
        return mType;
    }
}
