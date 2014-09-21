package com.elegion.rssreader.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.elegion.rssreader.R;
import com.elegion.rssreader.content.News;
import com.squareup.picasso.Picasso;

/**
 * Created by Andrey Kulikov on 20.09.14.
 */
public class NewsListAdapter extends ResourceCursorAdapter {

    public NewsListAdapter(Context context, Cursor c) {
        super(context, R.layout.li_news, c,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.tv_title)).setText(
                cursor.getString(cursor.getColumnIndex(News.Columns.TITLE)));
        String imageUrl = cursor.getString(cursor.
                getColumnIndex(News.Columns.IMAGE_URL));
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
        if (TextUtils.isEmpty(imageUrl)) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            Picasso.with(context).load(imageUrl).into(imageView);
        }
    }

}
