package com.elegion.rssreader.sync;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.elegion.rssreader.R;
import com.elegion.rssreader.activity.MainActivity;
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

    public static final int NTF_REQUEST = 12345;

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
            sendNotification();
        } catch (RetrofitError e) {
            Log.e("Retrofit", e.getMessage(), e);
        }
    }

    private RssService getService(String endpoint) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setConverter(new SimpleXMLConverter())
                .build()
                .create(RssService.class);
    }

    private void sendNotification() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), NTF_REQUEST,
                intent, PendingIntent.FLAG_ONE_SHOT);
        final Notification ntf = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.sync_notification_message))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.sync_notification_message)))
                .setTicker(getString(R.string.sync_notification_message))
                .setContentIntent(pendingIntent)
                .build();
        ntf.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManagerCompat.from(this).notify(NTF_REQUEST, ntf);
    }

}
