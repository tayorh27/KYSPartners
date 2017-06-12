package com.kys.kyspartners.network;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Callbacks.VerifyAccessCodeCallback;
import com.kys.kyspartners.General;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sanniAdewale on 30/03/2017.
 */

public class VerifyAccessCode {

    public Context context;
    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    String access_code;
    General general;
    VerifyAccessCodeCallback codeCallback;

    public VerifyAccessCode(Context context, String access_code, VerifyAccessCodeCallback codeCallback) {
        this.context = context;
        this.access_code = access_code;
        this.codeCallback = codeCallback;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        general = new General(context);
    }

    public void CheckAccessCode(final CardView cardView, final TextView textView) {

        String url = AppConfig.WEB_URL + "CheckAccess.php?access_code=" + access_code;
        general.displayDialog("Verifying access code");

        String _url = url.replace(" ", "%20");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, _url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.contentEquals("null")) {
                        general.dismissDialog();
                        general.error("Access Code cannot be verified");
                        return;
                    }
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject object = jsonArray.getJSONObject(0);
                    String _email = object.getString("email");
                    String username = object.getString("username");
                    cardView.setVisibility(View.VISIBLE);
                    textView.setText(_email);
                    general.dismissDialog();
                    if (codeCallback != null) {
                        codeCallback.onAccessCode(username);
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
}
