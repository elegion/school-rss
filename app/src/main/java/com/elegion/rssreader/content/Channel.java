package com.elegion.rssreader.content;

import android.net.Uri;

import com.elegion.rssreader.BuildConfig;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.Collections;
import java.util.List;

/**
 * @author Daniel Serdyukov
 */
@Root(name = "channel", strict = false)
public class Channel {

    public static final Uri URI = Uri.parse("content://" + BuildConfig.PACKAGE_NAME + "/channels");

    @Element(name = "title")
    private String mTitle;

    @Element(name = "link")
    private String mLink;

    @ElementList(name = "item", inline = true)
    private List<News> mNews;

    public List<News> getNews() {
        return mNews == null ? Collections.<News>emptyList() : mNews;
    }

}
