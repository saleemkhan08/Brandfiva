package com.shoppinfever.services;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.shoppinfever.MyApplication;

/**
 * Created by Saleem Khan on 10/31/2015.
 */
public class VolleySingleton
{
    public static VolleySingleton sInstance = null;
    private static RequestQueue mRequestQueue;
    private ImageLoader imageLoader;

    private VolleySingleton()
    {
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
        imageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache()
        {
            private LruCache<String,Bitmap> cache = new LruCache<>((int)Runtime.getRuntime().maxMemory()/1024/8);
            @Override
            public Bitmap getBitmap(String url)
            {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap)
            {
                cache.put(url, bitmap);
            }
        });
    }

    public static VolleySingleton getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new VolleySingleton();
        }
        return sInstance;
    }

    public RequestQueue getmRequestQueue()
    {
        return mRequestQueue;
    }

    public ImageLoader getImageLoader()
    {
        return imageLoader;
    }
}
