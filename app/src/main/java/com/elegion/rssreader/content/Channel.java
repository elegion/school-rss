package com.elegion.rssreader.content;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * @author Daniel Serdyukov
 */
@Root(name = "channel", strict = true)
public class Channel {

    @Element(name = "title")
    private String mTitle;

    @Element(name = "link")
    private String mLink;

    @ElementList(name = "item", inline = true)
    private List<News> mNews;

}
