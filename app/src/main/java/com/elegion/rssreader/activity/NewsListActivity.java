package com.elegion.rssreader.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.elegion.rssreader.R;
import com.elegion.rssreader.adapter.NewsListAdapter;
import com.elegion.rssreader.content.News;
import com.elegion.rssreader.loader.NewsLoader;

/**
 * @author Daniel Serdyukov
 */
public class NewsListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    public static final String NEWS_COUNT_PREFS = "news_count";
    private ListView mListView;

    private CursorAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        mListView = (ListView) findViewById(android.R.id.list);
        mListAdapter = new NewsListAdapter(this, null);
        mListView.setAdapter(mListAdapter);
        getLoaderManager().initLoader(R.id.news_loader, getIntent().getExtras(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void onPause() {
        mListView.setOnItemClickListener(null);
        super.onPause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (R.id.news_loader == id) {
            return new NewsLoader(getApplicationContext(), args.getLong(News.Columns.CHANNEL_ID));
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (R.id.news_loader == loader.getId()) {
            refreshTitle(data.getCount());
            mListAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (R.id.news_loader == loader.getId()) {
            mListAdapter.swapCursor(null);
        }
    }

    private void refreshTitle(int count) {
        // example for SharedPreferences
        SharedPreferences preferences = PreferenceManager.
                getDefaultSharedPreferences(this);
        preferences.edit()
                .putInt(NEWS_COUNT_PREFS, count)
                .commit();

        int countFromPrefs = preferences.getInt(NEWS_COUNT_PREFS, 0);
        if (getActionBar() != null) {
            getActionBar().setTitle(String.format("News count %d", countFromPrefs));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, NewsCardActivity.class);
        Cursor cursor = (Cursor) mListAdapter.getItem(position);
        intent.putExtra(News.Columns.TITLE, cursor.getString(
                cursor.getColumnIndex(News.Columns.TITLE)));
        intent.putExtra(News.Columns.FULL_TEXT, cursor.getString(
                cursor.getColumnIndex(News.Columns.FULL_TEXT)));
        intent.putExtra(News.Columns.IMAGE_URL, cursor.getString(
                cursor.getColumnIndex(News.Columns.IMAGE_URL)));
        startActivity(intent);
    }
}
