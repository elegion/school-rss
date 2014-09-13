package com.elegion.rssreader.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.elegion.rssreader.R;
import com.elegion.rssreader.content.News;
import com.elegion.rssreader.loader.AsyncRssLoader;

import java.util.List;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<List<News>> {

    private ListView mListView;

    private ArrayAdapter<News> mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        mListView = (ListView) findViewById(android.R.id.list);
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mListView.setAdapter(mListAdapter);
        getLoaderManager().initLoader(R.id.rss_loader, Bundle.EMPTY, this);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        if (R.id.rss_loader == id) {
            return new AsyncRssLoader(getApplicationContext());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        if (R.id.rss_loader == loader.getId()) {
            mListAdapter.clear();
            mListAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        if (R.id.rss_loader == loader.getId()) {
            mListAdapter.clear();
        }
    }

}
