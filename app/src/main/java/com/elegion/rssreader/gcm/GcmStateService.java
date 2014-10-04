package com.elegion.rssreader.gcm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import com.elegion.rssreader.BuildConfig;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * @author Daniel Serdyukov
 */
public class GcmStateService extends IntentService {

    private GoogleCloudMessaging mGcm;

    private SharedPreferences mSp;

    public GcmStateService() {
        super(GcmStateService.class.getSimpleName());
    }

    public static void start(@NonNull Context context) {
        context.startService(new Intent(context, GcmStateService.class));
    }

    private static boolean isRegIdExpired(long regTime) {
        return (System.currentTimeMillis() - regTime) > DateUtils.WEEK_IN_MILLIS;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGcm = GoogleCloudMessaging.getInstance(this);
        mSp = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            gcmRegister();
        } catch (IOException e) {
            Log.e("GcmStateService", e.getMessage(), e);
            retryGcmRegister();
        }
    }

    private void gcmRegister() throws IOException {
        String regId = mSp.getString("gcm_reg_id", null);
        if (!isValidRegId(regId)) {
            regId = mGcm.register(BuildConfig.GCM_SENDER_ID);
            if (!TextUtils.isEmpty(regId)) {
                onGcmRegistrationIdChanged(regId);
            } else {
                throw new IOException("registration_id is empty");
            }
        }
    }

    private boolean isValidRegId(@NonNull String regId) {
        return !TextUtils.isEmpty(regId)
                && !isRegIdExpired(mSp.getLong("gcm_reg_time", 0))
                && TextUtils.equals(BuildConfig.VERSION_NAME, mSp.getString("app_version_name", null));
    }

    private void onGcmRegistrationIdChanged(String regId) {
        Log.d("GcmStateService", regId);
        mSp.edit()
                .putString("gcm_reg_id", regId)
                .putString("app_version_name", BuildConfig.VERSION_NAME)
                .putLong("gcm_reg_time", System.currentTimeMillis())
                .apply();
    }

    private void retryGcmRegister() {
        final Intent retryIntent = new Intent(getApplicationContext(), GcmStateService.class);
        final long delay = 5 * DateUtils.SECOND_IN_MILLIS;
        final AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + delay, PendingIntent
                .getService(getApplicationContext(), 0, retryIntent, PendingIntent.FLAG_CANCEL_CURRENT));
    }

}