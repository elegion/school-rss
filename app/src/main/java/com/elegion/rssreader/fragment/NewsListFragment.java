package com.elegion.rssreader.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.elegion.rssreader.R;
import com.elegion.rssreader.adapter.NewsListAdapter;
import com.elegion.rssreader.loader.NewsLoader;

/**
 * Created by Andrey Kulikov on 24.09.14.
 */
public class NewsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private NewsListAdapter mListAdapter;
    private NewsParentListener mParentListener;

    private BroadcastReceiver mListPositionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setSelectedItemPosition(intent.getIntExtra(PagerFragment.POSITION_BUNDLE, 0));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new NewsListAdapter(getActivity(), null);
        setListAdapter(mListAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        onNewsSelected(position);
    }

    private void onNewsSelected(int position) {
        mParentListener.onNewsSelectedFromList(position,
                (Cursor) mListAdapter.getItem(position));
        setSelectedItemPosition(position);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mParentListener != null & mParentListener.isTwoFragmentVisibleMode()) {
            getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        }
        getLoaderManager().initLoader(R.id.news_loader, null, this);
    }

    private void setSelectedItemPosition(int position) {
        getListView().smoothScrollToPosition(position);
        if (mParentListener != null && mParentListener.isTwoFragmentVisibleMode()) {
            mListAdapter.setSelectedId(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mListPositionReceiver, new IntentFilter(PagerFragment.PAGE_CHANGED_ACTION));
        if (mParentListener != null && !mParentListener.isTwoFragmentVisibleMode()) {
            getActivity().getActionBar().setTitle("News");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mListPositionReceiver);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mParentListener = (NewsParentListener) activity;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (R.id.news_loader == id) {
            return new NewsLoader(getActivity().getApplicationContext(), 0);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mListAdapter.swapCursor(data);
        setListShown(true);
        if (mParentListener != null & mParentListener.isTwoFragmentVisibleMode()) {
            setSelectedItemPosition(0);
            onNewsSelected(0);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // do nothing
    }

    public interface NewsParentListener {
        void onNewsSelectedFromList(int position, Cursor cursor);
        boolean isTwoFragmentVisibleMode();
    }
}
