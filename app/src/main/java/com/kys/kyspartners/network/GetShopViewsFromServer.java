package com.kys.kyspartners.network;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Callbacks.ViewCallback;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.Shop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by sanniAdewale on 21/05/2017.
 */

public class GetShopViewsFromServer {

    public Context context;
    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    AppData data;
    Shop shop;
    General general;
    ViewCallback viewCallback;

    public GetShopViewsFromServer(Context context, ViewCallback viewCallback) {
        this.context = context;
        this.viewCallback = viewCallback;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        data = new AppData(context);
        general = new General(context);
        shop = data.getShop();
    }

    public void getShopViews() {
        String url = AppConfig.WEB_URL + "GetShopViews.php?shop_name=" + shop.name + "&type=Shop" + "&user_id=" + shop.user_id;

        String web_url = url.replace(" ", "%20");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, web_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contentEquals("null")) {
                    Toast.makeText(context, "No data available yet", Toast.LENGTH_SHORT).show();
                }
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        JSONObject object = jsonArray.getJSONObject(0);
                        int ratingCount = Integer.parseInt(object.getString("ratingCount"));
                        double rating = Double.parseDouble(object.getString("rating"));
                        BigDecimal bd = new BigDecimal(rating).setScale(1, RoundingMode.HALF_EVEN);
                        double final_rating = bd.doubleValue();
                        if (viewCallback != null) {
                            viewCallback.onView(jsonArray.length(), ratingCount, final_rating);
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
