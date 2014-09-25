package com.elegion.rssreader.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.elegion.rssreader.R;
import com.elegion.rssreader.fragment.NewsListFragment;
import com.elegion.rssreader.fragment.PagerFragment;

/**
 * @author Daniel Serdyukov
 */
public class NewsListActivity extends FragmentActivity implements NewsListFragment.NewsParentListener {

    private static final String LIST_TAG = "LIST_TAG";
    private static final String PAGER_TAG = "PAGER_TAG";

    private boolean mTwoFragmentVisibleMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_fragments);

        mTwoFragmentVisibleMode = (findViewById(R.id.fragment_container) == null);
        if (!isTwoFragmentVisibleMode()) {
            PagerFragment pagerFragment = (PagerFragment)
                    getSupportFragmentManager().findFragmentByTag(PAGER_TAG);
            if (pagerFragment != null) {
                // when before screen rotates we was in portrait orientation
                // on card fragment we need return to list fragment.
                // so, we need to back stack to previous fragment
                getSupportFragmentManager().popBackStack();
            }
            if (getSupportFragmentManager().findFragmentByTag(LIST_TAG) == null) {
                // if list fragment doesn't added - add
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new NewsListFragment(), LIST_TAG)
                        .commit();
            }
        }
    }

    @Override
    public void onNewsSelectedFromList(int position, Cursor cursor) {
        if (!isTwoFragmentVisibleMode()) {
            PagerFragment pagerFragment = PagerFragment.newFragment(position);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, pagerFragment, PAGER_TAG)
                    .addToBackStack(null)
                    .commit();
        } else {
            PagerFragment pagerFragment = (PagerFragment) getSupportFragmentManager()
                    .findFragmentByTag(getString(R.string.pager_from_xml_tag));
            pagerFragment.setPosition(position);
        }
    }

    @Override
    public boolean isTwoFragmentVisibleMode() {
        return mTwoFragmentVisibleMode;
    }
}
