package com.example.android.darb.other;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by scc pc-3 on 3/27/2018.
 */

public class Darb extends Application{

    public static final String TAG = Darb.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private static Darb mInstance;
    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized Darb getInstance() {
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the no_user tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }
}
