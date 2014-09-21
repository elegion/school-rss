package com.elegion.rssreader.sqlite;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteProvider extends ContentProvider {

    private static final String DATABASE_NAME = "rss.db";

    private static final int DATABASE_VERSION = 6;

    private SQLiteOpenHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new SQLiteHelperImpl(getContext(), DATABASE_NAME, DATABASE_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String where, String[] whereArgs, String orderBy) {
        final int match = SQLiteUriMatcher.match(uri);
        switch (match) {
            case SQLiteUriMatcher.MATCH_ID:
                return selectById(uri, columns);
            case SQLiteUriMatcher.MATCH_ALL:
                return selectAll(uri, columns, where, whereArgs, orderBy);
            default:
                throw new SQLiteException("Uri not found " + uri.toString());
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (SQLiteUriMatcher.match(uri) == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Uri not found " + uri.toString());
        }
        return insert(uri, values, true);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        if (SQLiteUriMatcher.match(uri) == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Uri not found " + uri.toString());
        }
        final int affectedRows = mHelper.getWritableDatabase().delete(uri.getPathSegments().get(0), where, whereArgs);
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        if (SQLiteUriMatcher.match(uri) == SQLiteUriMatcher.NO_MATCH) {
            throw new SQLiteException("Uri not found " + uri.toString());
        }
        final int affectedRows = mHelper.getWritableDatabase().update(uri.getPathSegments().get(0),
                values, where, whereArgs);
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            for (final ContentValues value : values) {
                insert(uri, value, false);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return values.length;
    }

    @Override
    public String getType(Uri uri) {
        final int match = SQLiteUriMatcher.match(uri);
        switch (match) {
            case SQLiteUriMatcher.MATCH_ID:
                return "vnd.android.cursor.item/" + uri.getPathSegments().get(0);
            case SQLiteUriMatcher.MATCH_ALL:
                return "vnd.android.cursor.dir/" + uri.getPathSegments().get(0);
            default:
                throw new SQLiteException("Uri not found " + uri.toString());
        }
    }

    private Cursor selectById(Uri uri, String[] columns) {
        return selectAll(uri, columns, BaseColumns._ID + "=?", new String[]{uri.getLastPathSegment()}, null);
    }

    private Cursor selectAll(Uri uri, String[] columns, String where, String[] whereArgs, String orderBy) {
        final Cursor cursor = mHelper.getReadableDatabase().query(
                uri.getPathSegments().get(0), columns, where,
                whereArgs, null, null, orderBy
        );
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values, boolean notify) {
        final long lastInsertRowid = mHelper.getWritableDatabase().insert(
                uri.getPathSegments().get(0),
                BaseColumns._ID,
                values
        );
        final Uri result = new Uri.Builder()
                .scheme(uri.getScheme())
                .authority(uri.getAuthority())
                .path(uri.getPathSegments().get(0))
                .path(String.valueOf(lastInsertRowid))
                .build();
        if (notify) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    private static final class SQLiteHelperImpl extends SQLiteOpenHelper {

        public SQLiteHelperImpl(Context context, String name, int version) {
            super(context, name, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS channels(_id INTEGER PRIMARY KEY, url TEXT, title TEXT, link TEXT);");
            db.execSQL("CREATE TABLE IF NOT EXISTS news(_id INTEGER PRIMARY KEY, title TEXT, " +
                    "link TEXT, pub_date TEXT, channel_id INTEGER," +
                    "full_text TEXT, image_url TEXT);");
            db.execSQL("CREATE INDEX IF NOT EXISTS news_idx1 ON news(channel_id);");
            db.execSQL("CREATE TRIGGER BEFORE DELETE ON channels" +
                    " BEGIN" +
                    " DELETE FROM news WHERE channel_id=OLD._id;" +
                    " END;");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS channels;");
            db.execSQL("DROP TABLE IF EXISTS news;");
            onCreate(db);
        }

    }

}
