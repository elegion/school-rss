package com.elegion.rssreader.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegion.rssreader.R;
import com.elegion.rssreader.content.News;
import com.elegion.rssreader.view.ObservableScrollView;
import com.squareup.picasso.Picasso;

public class NewsCardActivity extends Activity implements ObservableScrollView.OnScrollListener {

    private ImageView mImageView;
    private ObservableScrollView mObservableScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_news_card);

        if (getActionBar() != null) {
            getActionBar().setTitle(getIntent().getStringExtra(News.Columns.TITLE));
        }

        mImageView = (ImageView) findViewById(R.id.iv_icon);
        String imageUrl = getIntent().getStringExtra(News.Columns.IMAGE_URL);
        if (TextUtils.isEmpty(imageUrl)) {
            mImageView.setVisibility(View.GONE);
        } else {
            Picasso.with(this).load(imageUrl).into(mImageView);
        }

        mObservableScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);

        String fullText = getIntent().getStringExtra(News.Columns.FULL_TEXT);
        ((TextView) findViewById(R.id.tv_title)).setText(Html.fromHtml(fullText));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mObservableScrollView.setOnScrollListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mObservableScrollView.setOnScrollListener(null);
    }

    @Override
    public void onScroll(int y) {
        mImageView.setTranslationY(y / 2);
    }
}
