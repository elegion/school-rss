package com.elegion.rssreader.content;

import android.net.Uri;

import com.elegion.rssreader.BuildConfig;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @author Daniel Serdyukov
 */
@Root(name = "item", strict = false)
public class News {

    public static final Uri URI = Uri.parse("content://" + BuildConfig.PACKAGE_NAME + "/news");

    @Element(name = "title")
    private String mTitle;

    @Element(name = "link")
    private String mLink;

    @Element(name = "pubDate")
    private String mPubDate;

    @Override
    public String toString() {
        return mTitle;
    }

}
