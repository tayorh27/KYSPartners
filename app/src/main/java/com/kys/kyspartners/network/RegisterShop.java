package com.kys.kyspartners.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.kys.kyspartners.Activity.AddProductActivity;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.Shop;
import com.kys.kyspartners.Information.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanniAdewale on 12/05/2017.
 */

public class RegisterShop {

    Context context;
    General general;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    Shop shop;
    AppData data;
    User user;

    public RegisterShop(Context context, Shop shop) {
        this.context = context;
        general = new General(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.shop = shop;
        data = new AppData(context);
        user = data.getUser();
    }

    public void Register(final FABProgressCircle fabProgressCircle, final FloatingActionButton floatingActionButton, final Activity activity) {
        String url = AppConfig.WEB_URL + "RegisterShop.php";
        fabProgressCircle.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                int success;
                try {
                    JSONObject object = new JSONObject(response);
                    success = object.getInt("success");
                    if (success == 1) {
                        data.setShop(shop);
                        data.setShopAdded(true);
                        Toast.makeText(context, "Registration successful.", Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, AddProductActivity.class));
                        activity.finish();

                    } else if (success == 0) {
                        fabProgressCircle.setTag("register");
                        floatingActionButton.setEnabled(true);
                        fabProgressCircle.hide();
                        general.error("An error occurred. Check your internet connection.");
                    } else if (success == 2) {
                        fabProgressCircle.setTag("register");
                        floatingActionButton.setEnabled(true);
                        fabProgressCircle.hide();
                        general.error("An error occurred. Shop name already exists.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fabProgressCircle.setTag("register");
                fabProgressCircle.hide();
                floatingActionButton.setEnabled(true);
                general.error("An error occurred. Check your internet connection.");
            }
        }) {
            @Override
            public String getPostBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user.id + "");
                params.put("shop_name", shop.name);
                params.put("shop_description", shop.desc);
                params.put("shop_logo", shop.logo);
                params.put("shop_full_address", shop.full_add);
                params.put("shop_city", shop.city);
                params.put("shop_area", shop.area);
                params.put("shop_inside_area", shop.inside_area);
                params.put("phone_number", shop.phone_number);
                params.put("open_time", shop.open);
                params.put("close_time", shop.close);
                params.put("rating", "0");
                params.put("ratingCount", "0");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
