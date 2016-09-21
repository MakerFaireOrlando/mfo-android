package com.makerfaireorlando.mforlando.Network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class EventRestClient {
    private static final String BASE_URL = "https://www.googleapis.com/calendar/v3/calendars/orlandominimakerfaire.com_vffhp2b6oi3kiu3trnoo1502hg@group.calendar.google.com/events?key=";

    final int DEFAULT_TIMEOUT = 20 * 1000;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
        client.setTimeout(200000);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}