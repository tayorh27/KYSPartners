package com.kys.kyspartners.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.dd.processbutton.iml.ActionProcessButton;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.kys.kyspartners.Activity.AddProductActivity;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.Fragments.ExcelFragmentTab;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.Products;
import com.kys.kyspartners.Information.Shop;
import com.kys.kyspartners.Information.User;
import com.kys.kyspartners.MyApplication;
import com.kys.kyspartners.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pl.polak.clicknumberpicker.ClickNumberPickerView;

/**
 * Created by sanniAdewale on 12/05/2017.
 */

public class RegisterProducts {

    Context context;
    General general;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    Products products;
    AppData data;
    public int counter = 0;
    User user;
    ArrayList<Integer> failed_products = new ArrayList<>();

    public RegisterProducts(Context context, Products products) {
        this.context = context;
        general = new General(context);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        this.products = products;
        data = new AppData(context);
        user = data.getUser();
    }

    public RegisterProducts() {

    }

    public void Register(final ActionProcessButton actionProcessButton, final EditText[] editTexts, final AutoCompleteTextView aTV,
                         final BootstrapCircleThumbnail logo, final ClickNumberPickerView pickerView) {
        String url = AppConfig.WEB_URL + "RegisterProducts.php";
        actionProcessButton.setProgress(1);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                int success;
                try {
                    JSONObject object = new JSONObject(response);
                    success = object.getInt("success");
                    if (success == 1) {
                        counter++;
                        data.setProductAdded(true);
                        ArrayList<Products> p = new ArrayList<>();
                        p.add(products);
                        MyApplication.getWritableDatabase().insertMyProducts(p, false);
                        Toast.makeText(context, "Product uploaded.", Toast.LENGTH_SHORT).show();
                        actionProcessButton.setEnabled(true);
                        actionProcessButton.setProgress(0);
                        for (EditText et : editTexts) {
                            et.setText("");
                        }
                        aTV.setText("");
                        logo.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.no_logo));
                        pickerView.setPickerValue(1);
                        //context.startActivity(new Intent(context, AddProductActivity.class));

                    } else if (success == 0) {
                        actionProcessButton.setTag("register");
                        actionProcessButton.setEnabled(true);
                        actionProcessButton.setProgress(0);
                        general.error("An error occurred. Check your internet connection.");
                    } else if (success == 2) {
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
                params.put("user_id", user.id + "");
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

    public void RegisterFromExcel(final ProgressBar progressBar, final TextView textView, final int count) {
        String url = AppConfig.WEB_URL + "RegisterProducts.php";
        //failed_at_count = ExcelFragmentTab.count;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                int success;
                try {
                    JSONObject object = new JSONObject(response);
                    success = object.getInt("success");
                    if (success == 1) {
                        data.setProductAdded(true);
                        ArrayList<Products> p = new ArrayList<>();
                        p.add(products);
                        MyApplication.getWritableDatabase().insertMyProducts(p, false);
//                        int ct = ExcelFragmentTab.count++;
//                        progressBar.setIndeterminate(false);
//                        int percent = (ct / total) * 100;
//                        progressBar.setProgress(percent);
//                        textView.setText(percent + "%");
//                        Log.e("percent", percent + "%");
                        //ExcelFragmentTab.CallThread();
                        //Toast.makeText(context, "Product uploaded.", Toast.LENGTH_SHORT).show();
                        //context.startActivity(new Intent(context, AddProductActivity.class));

                    } else if (success == 2) {
                        failed_products.add(count);
                        Toast.makeText(context, "An error occurred. Check your internet connection..", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                failed_products.add(count);
                Toast.makeText(context, "An error occurred. Check your internet connection.", Toast.LENGTH_SHORT).show();
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
}
