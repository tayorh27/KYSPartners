package com.kys.kyspartners.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.kys.kyspartners.Activity.LoginActivity;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanniAdewale on 26/03/2017.
 */

public class RegisterShopUser {
    Context context;
    General general;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    String username, email, password, access_code;
    AppData data;

    public RegisterShopUser(Context context, String username, String email, String password, String access_code) {
        this.context = context;
        general = new General(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.username = username;
        this.email = email;
        this.password = password;
        this.access_code = access_code;
        data = new AppData(context);
    }

    public void Register(final BootstrapButton button, final Activity activity) {
        String url = AppConfig.WEB_URL + "ShopReg.php";

        String tag = button.getTag().toString();
        if (tag.contentEquals("email")) {
            SendEmail(activity);
            return;
        }
        general.displayDialog("Registering user");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                int success;
                try {
                    JSONObject object = new JSONObject(response);
                    success = object.getInt("success");
                    if (success == 1) {
                        general.dismissDialog();
                        button.setTag("email");
                        SendEmail(activity);
                        //User user = new User(0, username, email, password, access_code);
                        //data.setUser(user);
                        //data.setUserLoggedIn(true);
                        //
                    } else if (success == 0) {
                        general.dismissDialog();
                        general.error("An error occurred. Check your internet connection.");
                    } else if (success == 2) {
                        general.dismissDialog();
                        general.error("An error occurred. Email already exists.");
                    } else if (success == 3) {
                        general.dismissDialog();
                        general.error("An error occurred. Username already exists.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                general.dismissDialog();
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
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("access_code", access_code);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void SendEmail(final Activity activity) {
        String link = AppConfig.WEB_URL + "UpdateConfirmation.php?username=" + username;
        BackgroundMail.newBuilder(context)
                .withUsername(AppConfig.USERNAME)
                .withPassword(AppConfig.PASSKEY)
                .withMailto(email)
                .withType(BackgroundMail.TYPE_HTML)
                .withSubject("Email Confirmation")
                .withBody("Click on the link below to verify your email address.\n\n <a href='" + link + "'>Click here</a>")
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "Registration successful. Check your email now to verify.", Toast.LENGTH_LONG).show();
                        context.startActivity(new Intent(context, LoginActivity.class));
                        activity.finish();
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        general.error("An error occurred. Check your connectivity and try again.");
                    }
                })
                .send();
    }

}
