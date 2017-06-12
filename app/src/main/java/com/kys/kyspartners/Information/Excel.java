package com.kys.kyspartners.Information;

/**
 * Created by sanniAdewale on 13/05/2017.
 */

public class Excel {
    public int row_count, product_name, product_category, product_price, product_desc, stock;

    public Excel(int row_count, int product_name, int product_category, int product_price, int product_desc, int stock) {
        this.row_count = row_count;
        this.product_name = product_name;
        this.product_category = product_category;
        this.product_price = product_price;
        this.product_desc = product_desc;
        this.stock = stock;
    }
}
