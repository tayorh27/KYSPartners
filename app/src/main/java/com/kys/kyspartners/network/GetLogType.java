package com.kys.kyspartners.network;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Callbacks.LogTypeCallback;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.Shop;
import com.kys.kyspartners.Utility.LocationGetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sanniAdewale on 21/05/2017.
 */

public class GetLogType {

    public Context context;
    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    AppData data;
    Shop shop;
    General general;
    LogTypeCallback logCallback;

    public GetLogType(Context context, LogTypeCallback logCallback) {
        this.context = context;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.logCallback = logCallback;
        data = new AppData(context);
        general = new General(context);
        shop = data.getShop();
    }

    public void getShopLogByType(final String type, @Nullable final ImageView imageView) {
        String url = AppConfig.WEB_URL + "GetLogByType.php?shop_name=" + shop.name + "&type=" + type + "&user_id=" + shop.user_id;

        String web_url = url.replace(" ", "%20");
        final ArrayList<String> arrayList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, web_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contentEquals("null")) {
                    Toast.makeText(context, "No data available yet", Toast.LENGTH_SHORT).show();
                    if (logCallback != null) {
                        logCallback.onLogType(arrayList);
                    }
                }
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    String list = "";
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (type.contentEquals("Product")) {
                                list = jsonObject.getString("product_name");
                            } else {
                                list = jsonObject.getString("category_name");
                            }
                            arrayList.add(list);
                        }
                        if (logCallback != null) {
                            logCallback.onLogType(arrayList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                if (imageView != null) {
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        });
        requestQueue.add(stringRequest);

    }

    public void getShopLogLocation(final String filter) {
        String url = AppConfig.WEB_URL + "GetLogLocation.php?shop_name=" + shop.name + "&user_id=" + shop.user_id;

        String web_url = url.replace(" ", "%20");
        final ArrayList<String> arrayList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, web_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contentEquals("null")) {
                    Toast.makeText(context, "No data available yet", Toast.LENGTH_SHORT).show();
                    if (logCallback != null) {
                        logCallback.onLogType(arrayList);
                    }
                }
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    String list = "";
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String loc = jsonObject.getString("user_location");
                            list = LocationGetter.getEachLocation(loc, filter);
                            arrayList.add(list);
                        }
                        if (logCallback != null) {
                            logCallback.onLogType(arrayList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);

    }
}
