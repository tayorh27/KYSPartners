package com.kys.kyspartners.network;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.kys.kyspartners.Activity.AccessCodeActivity;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sanniAdewale on 30/03/2017.
 */

public class ForgotPasswordServer {

    public Context context;
    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    String email;
    General general;
    AppData data;

    public ForgotPasswordServer(Context context, String email) {
        this.context = context;
        this.email = email;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        general = new General(context);
        data = new AppData(context);
    }

    public void CheckUser() {

        String url = AppConfig.WEB_URL + "CheckUser.php?email=" + email;
        general.displayDialog("Confirming email address");

        String _url = url.replace(" ", "%20");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, _url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.contentEquals("null")) {
                        general.dismissDialog();
                        general.error("Email address does not exist");
                        return;
                    }
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject object = jsonArray.getJSONObject(0);
                    String _email = object.getString("email");
                    String access_code = object.getString("access_code");
                    general.dismissDialog();
                    SendEmail(_email, access_code);

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

    private void SendEmail(String email_, String access_code) {
        BackgroundMail.newBuilder(context)
                .withUsername(AppConfig.USERNAME)
                .withPassword(AppConfig.PASSKEY)
                .withMailto(email_)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Password Change Request")
                .withBody("Enter the below code to change your password.\n\n Access Code - " + access_code)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        context.startActivity(new Intent(context, AccessCodeActivity.class));
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
