package com.kys.kyspartners.Information;

/**
 * Created by sanniAdewale on 12/05/2017.
 */

public class Products {

    public int id, unique_value;
    public String shop_name, product_category, product_name, product_price, product_description, product_logo, inStock, type;

    public Products(int id, String shop_name, String product_category, String product_name, String product_price, String product_description, String product_logo, String inStock, String type, int unique_value) {
        this.id = id;
        this.shop_name = shop_name;
        this.product_category = product_category;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_description = product_description;
        this.product_logo = product_logo;
        this.inStock = inStock;
        this.type = type;
        this.unique_value = unique_value;
    }

    public Products() {

    }
}
