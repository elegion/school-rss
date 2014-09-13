package com.elegion.rssreader.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.elegion.rssreader.content.News;
import com.elegion.rssreader.content.Rss;

import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * @author Daniel Serdyukov
 */
public class AsyncRssLoader extends AsyncTaskLoader<List<News>> {

    private List<News> mNews;

    public AsyncRssLoader(Context context) {
        super(context);
    }

    @Override
    public List<News> loadInBackground() {
        final Persister persister = new Persister();
        try {
            final InputStream stream = new URL("http://news.yandex.ru/auto_racing.rss").openStream();
            try {
                final Rss rss = persister.read(Rss.class, stream);
                return rss.getChannel().getNews();
            } finally {
                stream.close();
            }
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public void deliverResult(List<News> data) {
        mNews = data;
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (mNews == null) {
            forceLoad();
        } else {
            deliverResult(mNews);
        }
    }

}
