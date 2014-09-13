package com.elegion.rssreader.content;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

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

    public ContentValues toValues(String url) {
        final ContentValues values = new ContentValues();
        values.put(Columns.URL, url);
        values.put(Columns.TITLE, mTitle);
        values.put(Columns.LINK, mLink);
        return values;
    }

    public static interface Columns extends BaseColumns {
        String URL = "url";
        String TITLE = "title";
        String LINK = "link";
    }

}
