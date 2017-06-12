package com.kys.kyspartners.network;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Callbacks.LogCallback;
import com.kys.kyspartners.Callbacks.RatingCallback;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.Comments;
import com.kys.kyspartners.Information.Log;
import com.kys.kyspartners.Information.Shop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sanniAdewale on 21/05/2017.
 */

public class GetLogFromServer {

    public Context context;
    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    public LogCallback logCallback;
    public RatingCallback ratingCallback;
    AppData data;
    Shop shop;
    General general;

    public GetLogFromServer(Context context, LogCallback logCallback, RatingCallback ratingCallback) {
        this.context = context;
        this.logCallback = logCallback;
        this.ratingCallback = ratingCallback;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        data = new AppData(context);
        general = new General(context);
        shop = data.getShop();
    }

    public void getShopLog() {
        String url = AppConfig.WEB_URL + "GetLog.php?shop_name=" + shop.name + "&user_id=" + shop.user_id;

        String web_url = url.replace(" ", "%20");
        final ArrayList<Log> arrayList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, web_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contentEquals("null")) {
                    Toast.makeText(context, "No feed available yet", Toast.LENGTH_SHORT).show();
                    if (logCallback != null) {
                        logCallback.onLogCallback(arrayList);
                    }
                }
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String product_name = jsonObject.getString("product_name");
                            String category_name = jsonObject.getString("category_name");
                            String rating = jsonObject.getString("rating");
                            String comment = jsonObject.getString("comment");
                            String d = jsonObject.getString("day");
                            String m = jsonObject.getString("month");
                            String y = jsonObject.getString("year");
                            String date = d + "/" + m + "/" + y;
                            String user_location = jsonObject.getString("user_location");
                            String type = jsonObject.getString("type");
                            Log log = new Log(product_name, category_name, rating, comment, date, user_location, type);
                            arrayList.add(log);
                        }
                        if (logCallback != null) {
                            logCallback.onLogCallback(arrayList);
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

    public void getRatings() {
        String url = AppConfig.WEB_URL2 + "ratings.php?shop_name=" + shop.name;
        final ArrayList<Comments> arrayList = new ArrayList<>();

        String _url = url.replace(" ", "%20");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, _url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contentEquals("null")) {
                    Toast.makeText(context, "No rating available yet", Toast.LENGTH_SHORT).show();
                    if (ratingCallback != null) {
                        ratingCallback.onRatingLoaded(arrayList);
                    }
                }
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String shopName = jsonObject.getString("shop_name");
                            String username = jsonObject.getString("username");
                            String title = jsonObject.getString("rating_title");
                            String comment = jsonObject.getString("rating_comment");
                            String star = jsonObject.getString("rating_star");
                            String items = jsonObject.getString("items");
                            String date = jsonObject.getString("rating_date");
                            Comments rating = new Comments(shopName, username, title, comment, star, items, date);
                            arrayList.add(rating);
                        }
                        if (ratingCallback != null) {
                            ratingCallback.onRatingLoaded(arrayList);
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
