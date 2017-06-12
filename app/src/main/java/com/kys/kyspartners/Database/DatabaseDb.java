package com.kys.kyspartners.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.kys.kyspartners.Information.Products;

import java.util.ArrayList;

/**
 * Created by sanniAdewale on 12/05/2017.
 */

public class DatabaseDb {

    ProductsHelper helper;
    SQLiteDatabase sqLiteDatabase;

    public DatabaseDb(Context context) {
        helper = new ProductsHelper(context);
        sqLiteDatabase = helper.getWritableDatabase();
    }

    public void insertMyProducts(ArrayList<Products> lists, boolean clearPrevious) {
        if (clearPrevious) {
            deleteAll();
        }
        String sql = "INSERT INTO " + ProductsHelper.TABLE_NAME_MYPOST + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);";
        //compile statement and start a transaction
        SQLiteStatement statement = sqLiteDatabase.compileStatement(sql);
        sqLiteDatabase.beginTransaction();

        for (int i = 0; i < lists.size(); i++) {
            Products current = lists.get(i);
            statement.clearBindings();

            statement.bindString(2, current.shop_name);
            statement.bindString(3, current.product_category);
            statement.bindString(4, current.product_name);
            statement.bindString(5, current.product_price);
            statement.bindString(6, current.product_description);
            statement.bindString(7, current.product_logo);
            statement.bindString(8, current.inStock);
            statement.bindString(9, current.type);
            statement.bindString(10, current.unique_value + "");
            statement.bindString(11, "");
            statement.bindString(12, "");
            statement.bindString(13, "");
            statement.execute();
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    public ArrayList<Products> getAllMyProducts() {
        ArrayList<Products> currentData = new ArrayList<>();

        String[] columns = {
                ProductsHelper.COLUMN_ID,
                ProductsHelper.COLUMN_SHOP_NAME,
                ProductsHelper.COLUMN_P_CATEGORY,
                ProductsHelper.COLUMN_P_NAME,
                ProductsHelper.COLUMN_P_PRICE,
                ProductsHelper.COLUMN_P_DESC,
                ProductsHelper.COLUMN_P_LOGO,
                ProductsHelper.COLUMN_P_STOCK,
                ProductsHelper.COLUMN_P_TYPE,
                ProductsHelper.COLUMN_P_UNIQUE
        };
        Cursor cursor = sqLiteDatabase.query(ProductsHelper.TABLE_NAME_MYPOST, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                Products current = new Products();
                current.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_ID)));
                current.shop_name = cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_SHOP_NAME));
                current.product_category = cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_P_CATEGORY));
                current.product_name = cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_P_NAME));
                current.product_price = cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_P_PRICE));
                current.product_description = cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_P_DESC));
                current.product_logo = cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_P_LOGO));
                current.inStock = cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_P_STOCK));
                current.type = cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_P_TYPE));
                current.unique_value = cursor.getInt(cursor.getColumnIndex(ProductsHelper.COLUMN_P_UNIQUE));
                currentData.add(current);
            }
            cursor.close();
        }

        return currentData;
    }

    public int getLastId() {
        int id = 0;
        String[] columns = {
                ProductsHelper.COLUMN_ID,
                ProductsHelper.COLUMN_SHOP_NAME,
                ProductsHelper.COLUMN_P_CATEGORY,
                ProductsHelper.COLUMN_P_NAME,
                ProductsHelper.COLUMN_P_PRICE,
                ProductsHelper.COLUMN_P_DESC,
                ProductsHelper.COLUMN_P_LOGO,
                ProductsHelper.COLUMN_P_STOCK,
                ProductsHelper.COLUMN_P_TYPE,
                ProductsHelper.COLUMN_P_UNIQUE
        };
        Cursor cursor = sqLiteDatabase.query(ProductsHelper.TABLE_NAME_MYPOST, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToLast();
            id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_ID)));
            //cursor.close();
        }
        Log.e("gotID", "my id is " + id);
        return id;
    }

    public void deleteAll() {
        sqLiteDatabase.delete(ProductsHelper.TABLE_NAME_MYPOST, null, null);
    }

    public void updateDatabase(Products products) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductsHelper.COLUMN_P_CATEGORY, products.product_category);
        contentValues.put(ProductsHelper.COLUMN_P_NAME, products.product_name);
        contentValues.put(ProductsHelper.COLUMN_P_PRICE, products.product_price);
        contentValues.put(ProductsHelper.COLUMN_P_DESC, products.product_description);
        contentValues.put(ProductsHelper.COLUMN_P_LOGO, products.product_logo);
        contentValues.put(ProductsHelper.COLUMN_P_STOCK, products.inStock);
        sqLiteDatabase.update(ProductsHelper.TABLE_NAME_MYPOST, contentValues, ProductsHelper.COLUMN_P_UNIQUE + "=" + products.unique_value, null);//
        Log.e("UPDATE", "database updated to ");
    }

    public void deleteDatabase(int unique_value) {
        sqLiteDatabase.delete(ProductsHelper.TABLE_NAME_MYPOST, ProductsHelper.COLUMN_P_UNIQUE + "=" + unique_value, null);
    }


    public class ProductsHelper extends SQLiteOpenHelper {

        private Context mContext;
        private static final String DB_NAME = "Products_db";
        private static final int DB_VERSION = 3;

        public static final String TABLE_NAME_MYPOST = "Products101";
        public static final String COLUMN_ID = "_id";

        public static final String COLUMN_SHOP_NAME = "shop_name";
        public static final String COLUMN_P_CATEGORY = "category";
        public static final String COLUMN_P_NAME = "product_name";
        public static final String COLUMN_P_PRICE = "price";
        public static final String COLUMN_P_DESC = "desc";
        public static final String COLUMN_P_LOGO = "logo";
        public static final String COLUMN_P_STOCK = "stock";
        public static final String COLUMN_P_TYPE = "type";
        public static final String COLUMN_P_UNIQUE = "unique_value";
        public static final String COLUMN_P_EXTRA1 = "extra1";
        public static final String COLUMN_P_EXTRA2 = "extra2";
        public static final String COLUMN_P_EXTRA3 = "extra3";

        private static final String CREATE_TABLE_MYPOST = "CREATE TABLE " + TABLE_NAME_MYPOST + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_SHOP_NAME + " TEXT," +
                COLUMN_P_CATEGORY + " TEXT," +
                COLUMN_P_NAME + " TEXT," +
                COLUMN_P_PRICE + " TEXT," +
                COLUMN_P_DESC + " TEXT," +
                COLUMN_P_LOGO + " TEXT," +
                COLUMN_P_STOCK + " TEXT," +
                COLUMN_P_TYPE + " TEXT," +
                COLUMN_P_UNIQUE + " TEXT," +
                COLUMN_P_EXTRA1 + " TEXT," +
                COLUMN_P_EXTRA2 + " TEXT," +
                COLUMN_P_EXTRA3 + " TEXT" +
                ");";


        public ProductsHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try {
                sqLiteDatabase.execSQL(CREATE_TABLE_MYPOST);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            try {
                sqLiteDatabase.execSQL(" DROP TABLE " + TABLE_NAME_MYPOST + " IF EXISTS;");
                onCreate(sqLiteDatabase);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }
}
