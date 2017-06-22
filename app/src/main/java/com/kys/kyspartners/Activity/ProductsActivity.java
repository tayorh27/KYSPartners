package com.kys.kyspartners.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kys.kyspartners.Adapters.ProductAdapter;
import com.kys.kyspartners.Callbacks.ProductsCallback;
import com.kys.kyspartners.Callbacks.ShopsClickListener;
import com.kys.kyspartners.Information.Products;
import com.kys.kyspartners.MyApplication;
import com.kys.kyspartners.R;
import com.kys.kyspartners.network.GetShopProducts;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductsActivity extends AppCompatActivity implements ShopsClickListener, ProductsCallback {

    RecyclerView recyclerView;
    TextView tv;
    ProductAdapter adapter;
    ArrayList<Products> customList = new ArrayList<>();
    String initial_load = "local";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tv = (TextView) findViewById(R.id.tv_info);
        adapter = new ProductAdapter(ProductsActivity.this, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(ProductsActivity.this));
        recyclerView.setAdapter(adapter);

        LoadRecycle();
    }

    private void LoadRecycle() {
        ArrayList<Products> productsArrayList = MyApplication.getWritableDatabase().getAllMyProducts();
        for (Products products : productsArrayList) {
            if (!products.shop_name.contentEquals("none")) {
                customList.add(products);
            }
        }
        if (customList.isEmpty()) {
            tv.setText("No product added yet");
            tv.setVisibility(View.VISIBLE);
        }
        adapter.LoadRecyclerView(customList);
    }

    private void LoadFromServer() {
        GetShopProducts getShopProducts = new GetShopProducts(ProductsActivity.this, this);
        getShopProducts.getProducts();
    }

    @Override
    public void onShopsClickListener(View view, int position) {
        Products products = customList.get(position);
        Intent intent = new Intent(ProductsActivity.this, EditProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", products.id);
        bundle.putString("logo", products.product_logo);
        bundle.putString("name", products.product_name);
        bundle.putString("desc", products.product_description);
        bundle.putString("cat", products.product_category);
        bundle.putString("price", products.product_price);
        bundle.putString("stock", products.inStock);
        bundle.putString("type", products.type);
        bundle.putInt("unique_value", products.unique_value);
        bundle.putString("load", initial_load);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            startActivity(new Intent(ProductsActivity.this, AddProductActivity.class));
        }
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.action_load) {
            LoadFromServer();
        }
        if (id == R.id.action_save) {
            SaveDataOffline();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SaveDataOffline() {
        if (initial_load.contentEquals("server")) {
            if (!customList.isEmpty()) {
                MyApplication.getWritableDatabase().deleteAll();
                ArrayList<Products> products = MyApplication.getWritableDatabase().getAllMyProducts();
                if (products.isEmpty()) {
                    Products p = new Products(0, "none", "", "", "", "", "", "", "", 0);
                    ArrayList<Products> c = new ArrayList<>();
                    c.add(p);
                    MyApplication.getWritableDatabase().insertMyProducts(c, false);
                }
                MyApplication.getWritableDatabase().insertMyProducts(customList, false);
                Toast.makeText(ProductsActivity.this, "Data successfully saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProductsActivity.this, "No data available yet.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ProductsActivity.this, "Please load data first.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProductsLoaded(ArrayList<Products> products) {
        if (!products.isEmpty()) {
            customList.clear();
            customList = products;
            adapter.LoadRecyclerView(customList);
            initial_load = "server";
            tv.setVisibility(View.GONE);
        }
        if (products.isEmpty()) {
            tv.setText("No product added yet");
            tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ArrayList<Products> products = MyApplication.getWritableDatabase().getAllMyProducts();
        if (products.isEmpty()) {
            Products p = new Products(0, "none", "", "", "", "", "", "", "", 0);
            ArrayList<Products> c = new ArrayList<>();
            c.add(p);
            MyApplication.getWritableDatabase().insertMyProducts(c, false);
        }
    }
}
