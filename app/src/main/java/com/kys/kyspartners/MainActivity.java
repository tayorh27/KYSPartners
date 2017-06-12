package com.kys.kyspartners;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kys.kyspartners.Activity.AddProductActivity;
import com.kys.kyspartners.Activity.LoginActivity;
import com.kys.kyspartners.Activity.OwnerActivity;
import com.kys.kyspartners.Information.Products;

import java.io.File;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Products> products = MyApplication.getWritableDatabase().getAllMyProducts();
        if (products.isEmpty()) {
            Products p = new Products(0, "none", "", "", "", "", "", "", "", 0);
            ArrayList<Products> c = new ArrayList<>();
            c.add(p);
            MyApplication.getWritableDatabase().insertMyProducts(c, false);
        }

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir1 = new File(root + "/KYS/");
        if (!myDir1.exists())
            myDir1.mkdirs();

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        thread.start();
    }
}
