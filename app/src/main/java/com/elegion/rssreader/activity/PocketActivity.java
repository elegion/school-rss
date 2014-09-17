package com.elegion.rssreader.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.elegion.rssreader.R;
import com.elegion.rssreader.content.ShareItem;

/**
 * @author artyom on 17/09/14.
 */
public class PocketActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView mListView;
    private CursorAdapter mPocketAdapter;

    static void start(Context context) {
        Intent i = new Intent(context, PocketActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_pocket);

        mListView = (ListView) findViewById(R.id.list);
        mPocketAdapter = new SimpleCursorAdapter(this, R.layout.li_pocket, null, new String[]{"title"}, new int[]{R.id.title}, 0);
        mListView.setAdapter(mPocketAdapter);

        getLoaderManager().initLoader(R.id.loader_share, Bundle.EMPTY, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ShareItem.URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPocketAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPocketAdapter.swapCursor(null);
    }
}
