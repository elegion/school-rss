package com.elegion.rssreader.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.rssreader.R;
import com.elegion.rssreader.content.News;
import com.elegion.rssreader.view.ObservableScrollView;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

/**
 * Created by Andrey Kulikov on 24.09.14.
 */
public class NewsCardFragment extends Fragment implements ObservableScrollView.OnScrollListener, View.OnClickListener {

    private ImageView mImageView;
    private ObservableScrollView mObservableScrollView;
    private Button mOpenSiteBtn;
    private TextView mTitle;
    private NewsObject mNewsObject;

    public static final String NEWS_OBJECT_BUNDLE = "NEWS_OBJECT_BUNDLE";

    private static class NewsObject implements Serializable {
        String mFullText;
        String mLink;
        String mImageUrl;
    }

    public static NewsCardFragment newFragment(Cursor cursor) {
        NewsCardFragment newsCardFragment = new NewsCardFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEWS_OBJECT_BUNDLE,
                getNewsObjectFromCursor(cursor));
        newsCardFragment.setArguments(bundle);
        return newsCardFragment;
    }

    public static NewsObject getNewsObjectFromCursor(Cursor cursor) {
        NewsObject newsObject = new NewsObject();
        newsObject.mFullText = cursor.getString(
                cursor.getColumnIndex(News.Columns.FULL_TEXT));
        newsObject.mLink = cursor.getString(
                cursor.getColumnIndex(News.Columns.LINK));
        newsObject.mImageUrl = cursor.getString(
                cursor.getColumnIndex(News.Columns.IMAGE_URL));
        return newsObject;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mNewsObject = (NewsObject) getArguments().
                    getSerializable(NEWS_OBJECT_BUNDLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mObservableScrollView.setOnScrollListener(this);
        mOpenSiteBtn.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mObservableScrollView.setOnScrollListener(null);
        mOpenSiteBtn.setOnClickListener(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ac_news_card, container, false);

        mImageView = (ImageView) view.findViewById(R.id.iv_icon);
        mObservableScrollView = (ObservableScrollView) view.findViewById(R.id.scroll_view);
        mOpenSiteBtn = (Button) view.findViewById(R.id.btn_open_site);
        mTitle = (TextView) view.findViewById(R.id.tv_title);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshViews(mNewsObject);
    }

    public void refreshViews(NewsObject newsObject) {
        if (newsObject != null) {
            mNewsObject = newsObject;
            if (TextUtils.isEmpty(newsObject.mImageUrl)) {
                mImageView.setVisibility(View.GONE);
            } else {
                Picasso.with(getActivity()).load(newsObject.mImageUrl).into(mImageView);
            }

            if (!TextUtils.isEmpty(newsObject.mFullText)) {
                mTitle.setText(Html.fromHtml(newsObject.mFullText));
            }
        }
    }

    @Override
    public void onScroll(int y) {
        mImageView.setTranslationY(y / 2);
    }

    @Override
    public void onClick(View v) {
        if (mNewsObject != null) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(mNewsObject.mLink)));
        }
    }
}
