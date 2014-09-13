package com.elegion.rssreader.loader;

import android.content.Context;
import android.content.CursorLoader;

import com.elegion.rssreader.content.Channel;

/**
 * @author Daniel Serdyukov
 */
public class ChannelsLoader extends CursorLoader {

    public ChannelsLoader(Context context) {
        super(context, Channel.URI, null, null, null, null);
    }

}
