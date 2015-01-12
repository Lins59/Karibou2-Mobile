package com.telnet.karibou;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import java.net.CookieHandler;
import java.net.CookieManager;

public class HttpToolbox {

    private static final String DEFAULT_TAG = "K2";
    private static HttpToolbox mInstance;
    private static Context mCtx;
    private static String pantieId;
    private static CookieManager cookieManager;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private HttpToolbox(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        // Cookie store
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized HttpToolbox getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HttpToolbox(context);
        }
        return mInstance;
    }

    public static String getPantieId() {
        return HttpToolbox.pantieId;
    }

    public static void setPantieId(String pantieId) {
        HttpToolbox.pantieId = pantieId;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            // mHttpClient = new DefaultHttpClient();
            //mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext(), new HttpClientStack(mHttpClient));

            /*cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());*/

            DefaultHttpClient mDefaultHttpClient = new DefaultHttpClient();

            final ClientConnectionManager mClientConnectionManager = mDefaultHttpClient.getConnectionManager();
            final HttpParams mHttpParams = mDefaultHttpClient.getParams();
            final ThreadSafeClientConnManager mThreadSafeClientConnManager = new ThreadSafeClientConnManager(mHttpParams, mClientConnectionManager.getSchemeRegistry());

            mDefaultHttpClient = new DefaultHttpClient(mThreadSafeClientConnManager, mHttpParams);

            final HttpStack httpStack = new HttpClientStack(mDefaultHttpClient);

            this.mRequestQueue = Volley.newRequestQueue(mCtx, httpStack);
        }
        return mRequestQueue;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? DEFAULT_TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(DEFAULT_TAG);

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}