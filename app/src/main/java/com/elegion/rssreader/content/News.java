package com.elegion.rssreader.content;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

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

    public ContentValues toValues(String channelId) {
        final ContentValues values = new ContentValues();
        values.put(Columns.TITLE, mTitle);
        values.put(Columns.LINK, mLink);
        values.put(Columns.PUB_DATE, mPubDate);
        values.put(Columns.CHANNEL_ID, channelId);
        return values;
    }

    public static interface Columns extends BaseColumns {
        String TITLE = "title";
        String LINK = "link";
        String PUB_DATE = "pub_date";
        String CHANNEL_ID = "channel_id";
    }

}
