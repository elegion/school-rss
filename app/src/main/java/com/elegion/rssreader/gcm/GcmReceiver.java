package com.elegion.rssreader.gcm;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.elegion.rssreader.R;
import com.elegion.rssreader.activity.MainActivity;

/**
 * @author Daniel Serdyukov
 */
public class GcmReceiver extends BroadcastReceiver {

    public static final int NTF_REQUEST = 12346;

    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotification(context, intent.getStringExtra("message"));
    }

    private void sendNotification(Context context, String message) {
        final Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, NTF_REQUEST,
                intent, PendingIntent.FLAG_ONE_SHOT);
        final Notification ntf = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setTicker(message)
                .setContentIntent(pendingIntent)
                .build();
        ntf.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManagerCompat.from(context).notify(NTF_REQUEST, ntf);
    }

}
