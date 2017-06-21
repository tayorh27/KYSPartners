package com.kys.kyspartners.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dd.processbutton.iml.ActionProcessButton;
import com.kys.kyspartners.Activity.LoginActivity;
import com.kys.kyspartners.Activity.ProductsActivity;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.Products;
import com.kys.kyspartners.Information.User;
import com.kys.kyspartners.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanniAdewale on 23/05/2017.
 */

public class UpdateProductById {

    Context context;
    General general;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    Products products;
    AppData data;
    User user;

    public UpdateProductById(Context context, Products products) {
        this.context = context;
        general = new General(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.products = products;
        data = new AppData(context);
        user = data.getUser();
    }

    public void UpdateById(final ActionProcessButton actionProcessButton, final Activity activity, final String[] c_params, final String... parameters) {
        String url = AppConfig.WEB_URL + "UpdateProduct.php?user_id=" + user.id;
        actionProcessButton.setProgress(1);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                int success;
                try {
                    JSONObject object = new JSONObject(response);
                    success = object.getInt("success");
                    if (success == 1) {
                        MyApplication.getWritableDatabase().updateDatabase(products);
                        AsyncUpdateProductName asyncUpdateProductName = new AsyncUpdateProductName(parameters);
                        asyncUpdateProductName.execute();
                        AsyncUpdateCategoryName asyncUpdateCategoryName = new AsyncUpdateCategoryName(c_params);
                        asyncUpdateCategoryName.execute();
                        Toast.makeText(context, "Product updated.", Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, ProductsActivity.class));
                        activity.finish();

                    } else if (success == 0) {
                        actionProcessButton.setTag("register");
                        actionProcessButton.setEnabled(true);
                        actionProcessButton.setProgress(0);
                        general.error("An error occurred. Check your internet connection.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                actionProcessButton.setTag("register");
                actionProcessButton.setProgress(0);
                actionProcessButton.setEnabled(true);
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
                params.put("id", products.id + "");
                params.put("shop_name", products.shop_name);
                params.put("product_category", products.product_category);
                params.put("product_name", products.product_name);
                params.put("product_price", products.product_price);
                params.put("product_description", products.product_description);
                params.put("product_logo", products.product_logo);
                params.put("inStock", products.inStock);
                params.put("type", products.type);
                params.put("unique_value", products.unique_value + "");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void UpdateProductName(String product_name, String old_product_name, String user_id) {
        String url = AppConfig.WEB_URL + "UpdateProductName.php?product_name=" + product_name + "&old_product_name=" + old_product_name + "&user_id=" + user_id;
        String _url = url.replace(" ", "%20");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, _url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int success;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(stringRequest);
    }

    private class AsyncUpdateProductName extends AsyncTask<Void, Void, Void> {

        String[] parameters;

        private AsyncUpdateProductName(String... parameters) {
            this.parameters = parameters;
        }

        @Override
        protected Void doInBackground(Void... params) {
            UpdateProductName(parameters[0], parameters[1], parameters[2]);
            return null;
        }
    }

    private void UpdateCategoryName(String category_name, String old_category_name, String user_id) {
        String url = AppConfig.WEB_URL + "UpdateCategoryName.php?category_name=" + category_name + "&old_category_name=" + old_category_name + "&user_id=" + user_id;
        String _url = url.replace(" ", "%20");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, _url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int success;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(stringRequest);
    }

    private class AsyncUpdateCategoryName extends AsyncTask<Void, Void, Void> {

        String[] parameters;

        private AsyncUpdateCategoryName(String... parameters) {
            this.parameters = parameters;
        }

        @Override
        protected Void doInBackground(Void... params) {
            UpdateCategoryName(parameters[0], parameters[1], parameters[2]);
            return null;
        }
    }
}
