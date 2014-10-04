package com.elegion.rssreader.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.elegion.rssreader.R;
import com.elegion.rssreader.content.Channel;
import com.elegion.rssreader.content.News;
import com.elegion.rssreader.loader.ChannelsLoader;
import com.elegion.rssreader.sync.SyncService;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.dialog_refresh_msg)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startService(SyncService.sync(getApplicationContext()));
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this,
                                    R.string.dialog_refresh_cancel,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (R.id.rss_loader == id) {
            return new ChannelsLoader(getApplicationContext());
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
