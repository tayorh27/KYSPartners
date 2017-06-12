package com.kys.kyspartners.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.kys.kyspartners.Activity.AddProductActivity;
import com.kys.kyspartners.Activity.OwnerActivity;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.Shop;
import com.kys.kyspartners.Information.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sanniAdewale on 26/03/2017.
 */

public class GetLoginFromServer {

    public Context context;
    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    String username, password;
    General general;
    AppData data;

    public GetLoginFromServer(Context context, String username, String password) {
        this.context = context;
        this.username = username;
        this.password = password;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        general = new General(context);
        data = new AppData(context);
    }

    public void LoginUser(final BootstrapButton button, final Activity activity) {

        String tag = button.getTag().toString();
        if (!tag.contentEquals("login")) {
            User user = data.getUser();
            getShopDetails(button, user.id, activity);
            return;
        }

        String url = AppConfig.WEB_URL + "LoginUser.php?username=" + username;
        general.displayDialog("Checking user login details");

        String _url = url.replace(" ", "%20");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, _url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.contentEquals("null")) {
                        general.dismissDialog();
                        general.error("Incorrect username");
                        return;
                    }
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject object = jsonArray.getJSONObject(0);
                    int id = object.getInt("id");
                    String getUsername = object.getString("username");
                    String email = object.getString("email");
                    String getPassword = object.getString("password");
                    String access_code = object.getString("access_code");
                    String confirm = object.getString("email_confirm");
                    if (confirm.contentEquals("false")) {
                        general.error("Email address not yet confirmed");
                        general.dismissDialog();
                        return;
                    }

                    if (!getPassword.contentEquals(password)) {
                        general.dismissDialog();
                        general.error("Incorrect Password");
                    } else {
                        User user = new User(id, getUsername, email, getPassword, access_code);
                        data.setUser(user);
                        data.setUserLoggedIn(false);
                        general.dismissDialog();
                        button.setTag("shop");
                        getShopDetails(button, id, activity);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                general.dismissDialog();
                general.error("An error occurred. Check your connectivity.");
            }
        });

        requestQueue.add(stringRequest);
    }

    private void getShopDetails(final BootstrapButton button, int user_id, final Activity activity) {
        general.displayDialog("Checking for shop details...");
        String url = AppConfig.WEB_URL + "GetLoginShop.php?user_id=" + user_id;

        String web_url = url.replace(" ", "%20");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, web_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contentEquals("null")) {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
                    data.setUserLoggedIn(true);
                    context.startActivity(new Intent(context, OwnerActivity.class));
                    activity.finish();
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int ud = jsonObject.getInt("user_id");
                    String name = jsonObject.getString("shop_name");
                    String desc = jsonObject.getString("shop_description");
                    String logo = jsonObject.getString("shop_logo");
                    String full_address = jsonObject.getString("shop_full_address");
                    String city = jsonObject.getString("shop_city");
                    String area = jsonObject.getString("shop_area");
                    String inside_area = jsonObject.getString("shop_inside_area");
                    String number = jsonObject.getString("phone_number");
                    String open = jsonObject.getString("open_time");
                    String close = jsonObject.getString("close_time");
                    Shop shop = new Shop(ud, name, desc, logo, full_address, city, area, inside_area, number, open, close);
                    data.setShop(shop);
                    data.setShopAdded(true);
                    data.setUserLoggedIn(true);
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
                    general.dismissDialog();
                    context.startActivity(new Intent(context, AddProductActivity.class));
                    activity.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                button.setTag("shop");
                general.dismissDialog();
                general.error("An error occurred. Check your connectivity.");
            }
        });
        requestQueue.add(stringRequest);
    }
}
