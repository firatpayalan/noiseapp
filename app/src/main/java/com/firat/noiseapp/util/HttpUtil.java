package com.firat.noiseapp.util;


import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.HttpEntity;

/**
 * Created by FIRAT on 11.12.2016.
 */
public class HttpUtil {
    public static final String BASE_URL = "http://139.59.145.205:3000";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context context,String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.post(context,getAbsoluteUrl(url),entity,"application/json",responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
