package com.elegion.rssreader.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.elegion.rssreader.R;
import com.elegion.rssreader.content.News;
import com.elegion.rssreader.loader.NewsLoader;

/**
 * @author Daniel Serdyukov
 */
public class NewsListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView mListView;

    private CursorAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        mListView = (ListView) findViewById(android.R.id.list);
        mListAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{News.Columns.TITLE},
                new int[]{android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mListView.setAdapter(mListAdapter);
        getLoaderManager().initLoader(R.id.news_loader, getIntent().getExtras(), this);
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
            mListAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (R.id.news_loader == loader.getId()) {
            mListAdapter.swapCursor(null);
        }
    }

}
