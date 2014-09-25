package com.elegion.rssreader.content;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.elegion.rssreader.BuildConfig;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;

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

    @ElementList(name = "enclosure", required = false, inline = true)
    private List<Enclosure> mEnclosures;

    @Element(name = "full-text", required = false)
    @Namespace(prefix = "yandex")
    private String mFullText;

    @Element(name = "description")
    private String mDescription;

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
        values.put(Columns.FULL_TEXT, !TextUtils.isEmpty(mFullText) ?
                mFullText : mDescription);
        values.put(Columns.IMAGE_URL, getImageUrl());
        return values;
    }

    private String getImageUrl() {
        if (mEnclosures != null) {
            for (Enclosure enclosure: mEnclosures) {
                if (!TextUtils.isEmpty(enclosure.getType()) &&
                   enclosure.getType().startsWith("image")) {
                    return enclosure.getUrl();
                }
            }
        }
        return null;
    }

    public static interface Columns extends BaseColumns {
        String TITLE = "title";
        String LINK = "link";
        String PUB_DATE = "pub_date";
        String CHANNEL_ID = "channel_id";
        String FULL_TEXT = "full_text";
        String IMAGE_URL = "image_url";
    }

}
