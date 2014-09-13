package com.elegion.rssreader.loader;

import android.content.Context;
import android.content.CursorLoader;

import com.elegion.rssreader.content.News;

/**
 * @author Daniel Serdyukov
 */
public class NewsLoader extends CursorLoader {

    public NewsLoader(Context context) {
        super(context, News.URI, null, null, null, null);
    }

}
