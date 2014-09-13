package com.elegion.rssreader.content;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @author Daniel Serdyukov
 */
@Root(name = "rss", strict = true)
public class Rss {

    @Element(name = "channel")
    private Channel mChannel;

}
