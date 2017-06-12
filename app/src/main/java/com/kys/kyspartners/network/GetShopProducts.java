package com.kys.kyspartners.network;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Callbacks.ProductsCallback;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.General;
import com.kys.kyspartners.Information.Products;
import com.kys.kyspartners.Information.Shop;
import com.kys.kyspartners.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sanniAdewale on 22/05/2017.
 */

public class GetShopProducts {

    public Context context;
    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    public ProductsCallback productsCallback;
    General general;
    AppData data;
    Shop shop;

    public GetShopProducts(Context context, ProductsCallback productsCallback) {
        this.context = context;
        this.productsCallback = productsCallback;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        general = new General(context);
        data = new AppData(context);
        shop = data.getShop();
    }

    public void getProducts() {
        general.displayDialog("Downloading products from server...");
        String url = AppConfig.WEB_URL + "ShopProduct.php?shop_name=" + shop.name + "&user_id=" + shop.user_id;
        final ArrayList<Products> arrayList = new ArrayList<>();

        String _url = url.replace(" ", "%20");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, _url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contentEquals("null")) {
                    general.dismissDialog();
                    Toast.makeText(context, "No product available yet", Toast.LENGTH_SHORT).show();
                }
                general.dismissDialog();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String shop_name = jsonObject.getString("shop_name");
                            String product_category = jsonObject.getString("product_category");
                            String product_name = jsonObject.getString("product_name");
                            String product_price = jsonObject.getString("product_price");
                            String product_description = jsonObject.getString("product_description");
                            String product_logo = jsonObject.getString("product_logo");
                            String inStock = jsonObject.getString("inStock");
                            String type = jsonObject.getString("type");
                            int uv = jsonObject.getInt("unique_value");
                            arrayList.add(new Products(id, shop_name, product_category, product_name, product_price, product_description, product_logo, inStock, type, uv));
                        }
                        ArrayList<Products> products = MyApplication.getWritableDatabase().getAllMyProducts();
                        if (products.isEmpty()) {
                            Products p = new Products(0, "none", "", "", "", "", "", "", "", 0);
                            ArrayList<Products> c = new ArrayList<>();
                            c.add(p);
                            MyApplication.getWritableDatabase().insertMyProducts(c, false);
                        }
                        if (productsCallback != null) {
                            productsCallback.onProductsLoaded(arrayList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                general.dismissDialog();
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }
}
