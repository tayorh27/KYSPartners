package com.kys.kyspartners.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kys.kyspartners.Activity.HomeActivity;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sanniAdewale on 30/03/2017.
 */

public class UpdateUserPassword {

    Context context;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    String email, password, access_code;
    General general;
    User user;
    AppData data;

    public UpdateUserPassword(Context context, String email, String password, String access_code) {
        this.context = context;
        this.email = email;
        this.password = password;
        this.access_code = access_code;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        general = new General(context);
        data = new AppData(context);
        user = data.getUser();
    }

    public void UpdatePassword(final Activity activity) {
        String url = AppConfig.WEB_URL + "UpdateUserPassword.php?email=" + email + "&password=" + password + "&access_code=" + access_code;
        //String _url = url.replace(" ", "%20");
        general.displayDialog("Updating user password");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int success;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");
                    if (success == 1) {
                        general.dismissDialog();
                        User new_user = new User(user.id, user.username, user.email, password, access_code);
                        data.setUser(new_user);
                        Toast.makeText(context, "Password change successfully", Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, HomeActivity.class));
                        activity.finish();
                    } else {
                        general.dismissDialog();
                        general.error("An error occurred. Check your internet connectivity");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                general.dismissDialog();
                general.error("An error occurred. Check your internet connectivity");
            }
        });
        requestQueue.add(stringRequest);
    }

    public void UpdateUserEmail(final TextView tvEmail) {
        String url = AppConfig.WEB_URL + "UpdateEmail.php?email=" + email + "&id=" + user.id;
        //String _url = url.replace(" ", "%20");
        general.displayDialog("Updating user email address");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int success;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");
                    if (success == 1) {
                        general.dismissDialog();
                        //User new_user = new User(user.id, user.username, email, user.mobile, user.password, user.image_path, user.local_path);
                        //data.setUser(new_user);
                        User myUser = data.getUser();
                        tvEmail.setText(myUser.email);
                        Toast.makeText(context, "Email address changed successfully", Toast.LENGTH_SHORT).show();
                    } else if (success == 2) {
                        //general.dismissProgress();
                        //general.showAlert("An error occurred. Email already exists.");
                    } else {
                        general.dismissDialog();
                        general.error("An error occurred. Check your internet connectivity");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                general.dismissDialog();
                general.error("An error occurred. Check your internet connectivity");
            }
        });
        requestQueue.add(stringRequest);
    }

    public void UpdateUserPhone(final String number, final TextView tvPhone) {
        String url = AppConfig.WEB_URL + "UpdatePhone.php?number=" + number + "&id=" + user.id;
        //String _url = url.replace(" ", "%20");
        general.displayDialog("Updating user phone number");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int success;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");
                    if (success == 1) {
                        general.dismissDialog();
                        //User new_user = new User(user.id, user.username, user.email, number, user.password, user.image_path, user.local_path);
                        // data.setUser(new_user);
                        User myUser = data.getUser();
                        // tvPhone.setText(myUser.mobile);
                        Toast.makeText(context, "Phone Number changed successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        general.dismissDialog();
                        general.error("An error occurred. Check your internet connectivity");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                general.dismissDialog();
                general.error("An error occurred. Check your internet connectivity");
            }
        });
        requestQueue.add(stringRequest);
    }
}
