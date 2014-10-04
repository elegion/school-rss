package com.elegion.rssreader;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.format.DateUtils;

import com.elegion.rssreader.gcm.GcmStateService;
import com.elegion.rssreader.sync.SyncService;

/**
 * @author Daniel Serdyukov
 */
public class AppDelegate extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GcmStateService.start(this);
        final Intent intent = SyncService.sync(getApplicationContext());
        final AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC, 0, 20 * DateUtils.MINUTE_IN_MILLIS,
                PendingIntent.getService(getApplicationContext(), 100500, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
    }

}
