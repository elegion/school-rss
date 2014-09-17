package com.elegion.rssreader.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.elegion.rssreader.R;
import com.elegion.rssreader.content.Channel;
import com.elegion.rssreader.content.News;
import com.elegion.rssreader.loader.AsyncRssLoader;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private static final int SELECT_PICTURE = 777;
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
                new String[]{Channel.Columns.TITLE},
                new int[]{android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mListView.setAdapter(mListAdapter);
        getLoaderManager().initLoader(R.id.rss_loader, Bundle.EMPTY, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            PocketActivity.start(this);
        } else if (item.getItemId() == R.id.picture) {
            selectImageFromGallery();
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectImageFromGallery() {
        Intent showGallery = new Intent(Intent.ACTION_GET_CONTENT);
        showGallery.setType("image/*");
        startActivityForResult(Intent.createChooser(showGallery,
                                                    "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            data.getExtras();
        }
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
        if (R.id.rss_loader == id) {
            return new AsyncRssLoader(getApplicationContext(), "http://news.yandex.ru/", "auto_racing.rss");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (R.id.rss_loader == loader.getId()) {
            mListAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (R.id.rss_loader == loader.getId()) {
            mListAdapter.swapCursor(null);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Intent intent = new Intent(this, NewsListActivity.class);
        intent.putExtra(News.Columns.CHANNEL_ID, id);
        startActivity(intent);
    }

}
