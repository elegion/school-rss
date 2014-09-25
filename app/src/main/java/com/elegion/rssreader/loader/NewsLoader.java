package com.elegion.rssreader.loader;

import android.content.Context;
import android.support.v4.content.CursorLoader;

import com.elegion.rssreader.content.News;

/**
 * @author Daniel Serdyukov
 */
public class NewsLoader extends CursorLoader {

    public NewsLoader(Context context, long channelId) {
        super(context, News.URI, null, null, null, null);
    }

}
