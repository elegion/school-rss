package com.elegion.rssreader.sync;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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
public class SyncService extends IntentService {

    public static final String EXTRA_FEED_ID = "feed_id";

    public static final String EXTRA_ENDPOINT = "endpoint";

    public SyncService() {
        super("SyncService");
    }

    public static Intent sync(@NonNull Context context) {
        final Intent intent = new Intent(context, SyncService.class);
        intent.putExtra(SyncService.EXTRA_ENDPOINT, "http://www.vesti.ru/");
        intent.putExtra(SyncService.EXTRA_FEED_ID, "vesti.rss");
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            final String feedUrl = Uri.parse(intent.getStringExtra(EXTRA_ENDPOINT))
                    .buildUpon()
                    .appendPath(intent.getStringExtra(EXTRA_FEED_ID))
                    .build()
                    .toString();
            final Channel channel = getService(intent.getStringExtra(EXTRA_ENDPOINT))
                    .load(intent.getStringExtra(EXTRA_FEED_ID))
                    .getChannel();

            final ContentResolver db = getContentResolver();
            db.delete(Channel.URI, Channel.Columns.URL + "=?", new String[]{feedUrl});
            final Uri uri = db.insert(Channel.URI, channel.toValues(feedUrl));
            // content://com.elegion.rssreader/channels/1

            final List<News> news = channel.getNews();
            final ContentValues[] bulkNews = new ContentValues[news.size()];
            for (int i = 0; i < news.size(); ++i) {
                bulkNews[i] = news.get(i).toValues(uri.getLastPathSegment());
            }
            db.bulkInsert(News.URI, bulkNews);
            Log.d("SyncService", "Inserted " + db.bulkInsert(News.URI, bulkNews) + " rows");
        } catch (RetrofitError e) {
            Log.e("Retrofit", e.getMessage(), e);
        }
    }

    RssService getService(String endpoint) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setConverter(new SimpleXMLConverter())
                .build()
                .create(RssService.class);
    }

}
