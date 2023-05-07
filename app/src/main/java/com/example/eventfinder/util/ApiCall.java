package com.example.eventfinder.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventfinder.Fragment.SearchFragment;

public class ApiCall {
    private static ApiCall instance;
    private RequestQueue requestQueue;
    private static Context context;
    public ApiCall(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }
    public static synchronized ApiCall getInstance(Context context) {
        if (instance == null) {
            instance = new ApiCall(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }
    // can be used to pass in any types of request
    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    // make String request
    public static void make(Context context, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        Log.d("Called URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, listener, errorListener);
        ApiCall.getInstance(context).addToRequestQueue(stringRequest);
    }
}
