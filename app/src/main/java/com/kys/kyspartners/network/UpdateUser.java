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
import com.kys.kyspartners.Activity.LoginActivity;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.General;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sanniAdewale on 30/03/2017.
 */

public class UpdateUser {

    Context context;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    String email, password, access_code;
    General general;

    public UpdateUser(Context context, String email, String password, String access_code) {
        this.context = context;
        this.email = email;
        this.password = password;
        this.access_code = access_code;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        general = new General(context);
    }

    public void UpdateUser(final Activity activity) {
        String url = AppConfig.WEB_URL + "UpdateUser.php?email=" + email + "&password=" + password + "&access_code=" + access_code;
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
                        Toast.makeText(context, "Password change successfully", Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, LoginActivity.class));
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
}
