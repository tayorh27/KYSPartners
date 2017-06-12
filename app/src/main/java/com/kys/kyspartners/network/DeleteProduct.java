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
import com.kys.kyspartners.Activity.ProductsActivity;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sanniAdewale on 23/05/2017.
 */

public class DeleteProduct {

    Context context;
    General general;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;

    public DeleteProduct(Context context) {
        this.context = context;
        general = new General(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    public void Delete(final int uv, final Activity activity) {
        general.displayDialog("Deleting product.");
        String url = AppConfig.WEB_URL + "DeleteProduct.php?unique_value=" + uv;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                int success;
                try {
                    JSONObject object = new JSONObject(response);
                    success = object.getInt("success");
                    if (success == 1) {
                        general.dismissDialog();
                        MyApplication.getWritableDatabase().deleteDatabase(uv);
                        Toast.makeText(context, "Product deleted.", Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, ProductsActivity.class));
                        activity.finish();

                    } else if (success == 0) {
                        general.dismissDialog();
                        general.error("An error occurred. Check your internet connection.");
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
        });
        requestQueue.add(stringRequest);
    }

}
