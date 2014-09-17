package com.elegion.rssreader.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.elegion.rssreader.R;
import com.elegion.rssreader.content.ShareItem;

/**
 * @author artyom on 17/09/14.
 */
public class ShareActivity extends Activity {

    private TextView mOutputText;
    private View mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_share);

        mOutputText = (TextView) findViewById(R.id.output);
        mSaveButton = findViewById(R.id.save);
        Intent intent = getIntent();

        String text = intent.getStringExtra("text");
        if (!TextUtils.isEmpty(text)) {
            mOutputText.setText(text);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                ContentValues cv = new ContentValues();
                cv.put("content", intent.getStringExtra("text"));
                cv.put("title", intent.getStringExtra("title"));

                Uri result = getContentResolver().insert(ShareItem.URI, cv);
            }
        });
    }
}
