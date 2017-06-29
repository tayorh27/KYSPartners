package com.kys.kyspartners.Database;

import android.content.Context;
import android.content.SharedPreferences;

import com.kys.kyspartners.Information.Excel;
import com.kys.kyspartners.Information.Shop;
import com.kys.kyspartners.Information.User;

/**
 * Created by sanniAdewale on 11/05/2017.
 */

public class AppData {

    Context context;
    SharedPreferences prefs;

    public AppData(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("app_data", 0);
    }

    public void setShop(Shop shop) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("user_id", shop.user_id);
        editor.putString("name", shop.name);
        editor.putString("desc", shop.desc);
        editor.putString("logo", shop.logo);
        editor.putString("fullAddress", shop.full_add);
        editor.putString("city", shop.city);
        editor.putString("area", shop.area);
        editor.putString("inside_area", shop.inside_area);
        editor.putString("phone_number", shop.phone_number);
        editor.putString("open", shop.open);
        editor.putString("close", shop.close);
        editor.apply();
    }

    public Shop getShop() {
        int user_id = prefs.getInt("user_id", 0);
        String name = prefs.getString("name", "");
        String desc = prefs.getString("desc", "");
        String logo = prefs.getString("logo", "");
        String fullAddress = prefs.getString("fullAddress", "");
        String city = prefs.getString("city", "");
        String area = prefs.getString("area", "");
        String inside_area = prefs.getString("inside_area", "");
        String phone_number = prefs.getString("phone_number", "");
        String open = prefs.getString("open", "");
        String close = prefs.getString("close", "");
        return new Shop(user_id, name, desc, logo, fullAddress, city, area, inside_area, phone_number, open, close);
    }

    public void setUser(User user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("id", user.id);
        editor.putString("username", user.username);
        editor.putString("email", user.email);
        editor.putString("password", user.password);
        editor.putString("access_code", user.access_code);
        editor.apply();
    }

    public User getUser() {
        int id = prefs.getInt("id", 0);
        String username = prefs.getString("username", "");
        String email = prefs.getString("email", "");
        String password = prefs.getString("password", "");
        String access_code = prefs.getString("access_code", "");
        return new User(id, username, email, password, access_code);
    }

    public void setUserLoggedIn(boolean logged) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("logged", logged);
        editor.apply();
    }

    public boolean getUserLoggedIn() {
        return prefs.getBoolean("logged", false);
    }

    public void setShopAdded(boolean shop_added) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("shop_added", shop_added);
        editor.apply();
    }

    public boolean getShopAdded() {
        return prefs.getBoolean("shop_added", false);
    }

    public void setProductAdded(boolean product_added) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("product_added", product_added);
        editor.apply();
    }

    public boolean getProductAdded() {
        return prefs.getBoolean("product_added", false);
    }


    public void setLatLng(String latitude, String longitude) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("latitude", latitude);
        editor.putString("longitude", longitude);
        editor.apply();
    }

    public String getLatitude() {
        return prefs.getString("latitude", "0.0");
    }

    public String getLongitude() {
        return prefs.getString("longitude", "0.0");
    }

    public void setExcelSettings(Excel excel) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("row_before", excel.row_count);
        editor.putInt("product_name", excel.product_name);
        editor.putInt("product_category", excel.product_category);
        editor.putInt("product_price", excel.product_price);
        editor.putInt("product_desc", excel.product_desc);
        editor.putInt("product_logo", excel.product_logo);
        editor.putInt("stock", excel.stock);
        editor.apply();
    }

    public Excel getExcelSettings() {
        int row_before = prefs.getInt("row_before", 0);
        int p_name = prefs.getInt("product_name", 0);
        int p_category = prefs.getInt("product_category", 0);
        int p_price = prefs.getInt("product_price", 0);
        int p_desc = prefs.getInt("product_desc", 0);
        int p_logo = prefs.getInt("product_logo", 0);
        int p_stock = prefs.getInt("stock", 0);

        return new Excel(row_before, p_name, p_category, p_price, p_desc, p_logo, p_stock);
    }

    public void setExcelSettingsAdded(boolean excel_added) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("excel_added", excel_added);
        editor.apply();
    }

    public boolean getExcelSettingsAdded() {
        return prefs.getBoolean("excel_added", false);
    }

    public void SignOut() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
