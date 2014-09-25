package com.elegion.rssreader.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.rssreader.R;
import com.elegion.rssreader.adapter.NewsPagerAdapter;
import com.elegion.rssreader.loader.NewsLoader;

/**
 * Created by Andrey Kulikov on 24.09.14.
 */
public class PagerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String POSITION_BUNDLE = "position";
    public static final String PAGE_CHANGED_ACTION = "page_changed";

    private NewsPagerAdapter mNewsPagerAdapter;
    private ViewPager mViewPager;
    private int mPosition;

    public static PagerFragment newFragment(int position) {
        PagerFragment fragment = new PagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION_BUNDLE, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(POSITION_BUNDLE, 0);
        }
        mNewsPagerAdapter = new NewsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mNewsPagerAdapter);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                // do nothing
            }

            @Override
            public void onPageSelected(int i) {
                if (getActivity().getActionBar() != null) {
                    getActivity().getActionBar().setTitle(
                            mNewsPagerAdapter.getTitle(i));
                }
                // send broadcast with new position of list
                Intent intent = new Intent(PAGE_CHANGED_ACTION);
                intent.putExtra(POSITION_BUNDLE, i);
                LocalBroadcastManager.getInstance(getActivity()).
                        sendBroadcast(intent);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                // do nothing
            }
        });
        getLoaderManager().initLoader(R.id.news_loader, null, this);
        return view;
    }

    public void setPosition(int position) {
        if (mNewsPagerAdapter != null) {
            // change page without animation
            mViewPager.setCurrentItem(position, false);
        } else {
            mPosition = position;
        }
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
        mNewsPagerAdapter.swapCursor(data);
        setPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // do nothing
    }
}
