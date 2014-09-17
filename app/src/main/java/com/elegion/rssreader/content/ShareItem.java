package com.elegion.rssreader.content;

import android.net.Uri;

import com.elegion.rssreader.BuildConfig;

/**
 * @author artyom on 17/09/14.
 */
public class ShareItem {
    public static final Uri URI = Uri.parse("content://" + BuildConfig.PACKAGE_NAME + "/share");

    String mContent;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    String mTitle;
}
