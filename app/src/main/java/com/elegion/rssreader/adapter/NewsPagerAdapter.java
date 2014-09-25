package com.elegion.rssreader.adapter;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.elegion.rssreader.content.News;
import com.elegion.rssreader.fragment.NewsCardFragment;

/**
 * Created by Andrey Kulikov on 24.09.14.
 */
public class NewsPagerAdapter extends FragmentPagerAdapter {

    private Cursor mCursor;

    public NewsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if (mCursor.moveToPosition(i)) {
            return NewsCardFragment.newFragment(mCursor);
        }
        return null;
    }

    public String getTitle(int i) {
        mCursor.moveToPosition(i);
        return mCursor.getString(mCursor.getColumnIndex(News.Columns.TITLE));
    }

    @Override
    public int getCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

}
