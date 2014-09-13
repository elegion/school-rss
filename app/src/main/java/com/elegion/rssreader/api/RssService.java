package com.elegion.rssreader.api;

import com.elegion.rssreader.content.Rss;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * @author Daniel Serdyukov
 */
public interface RssService {

    @GET("/{id}")
    Rss load(@Path("id") String id);

}
