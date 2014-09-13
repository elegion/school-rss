package com.elegion.rssreader.loader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.elegion.rssreader.api.RssService;
import com.elegion.rssreader.content.Channel;
import com.elegion.rssreader.content.News;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.SimpleXMLConverter;

/**
 * @author Daniel Serdyukov
 */
public class AsyncRssLoader extends ChannelsLoader {

    private final String mEndpoint;

    private final String mId;

    private final String mFeedUrl;

    public AsyncRssLoader(Context context, String endpoint, String id) {
        super(context);
        mEndpoint = endpoint;
        mId = id;
        mFeedUrl = Uri.parse(endpoint)
                .buildUpon()
                .appendPath(id)
                .build()
                .toString();
    }

    @Override
    public Cursor loadInBackground() {
        try {
            final Channel channel = getService(mEndpoint).load(mId).getChannel();

            final ContentResolver db = getContext().getContentResolver();
            db.delete(Channel.URI, Channel.Columns.URL + "=?", new String[]{mFeedUrl});
            final Uri uri = db.insert(Channel.URI, channel.toValues(mFeedUrl));
            // content://com.elegion.rssreader/channels/1

            final List<News> news = channel.getNews();
            final ContentValues[] bulkNews = new ContentValues[news.size()];
            for (int i = 0; i < news.size(); ++i) {
                bulkNews[i] = news.get(i).toValues(uri.getLastPathSegment());
            }
            db.bulkInsert(News.URI, bulkNews);
        } catch (RetrofitError e) {
            Log.e("Retrofit", e.getMessage(), e);
        }

        return super.loadInBackground();
    }

    RssService getService(String endpoint) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setConverter(new SimpleXMLConverter())
                .build()
                .create(RssService.class);
    }

}
