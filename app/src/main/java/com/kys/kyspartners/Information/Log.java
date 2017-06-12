package com.kys.kyspartners.Information;

/**
 * Created by sanniAdewale on 21/05/2017.
 */

public class Log {

    public String product_name, category_name, rating, comment, date, user_location, type;

    public Log(String product_name, String category_name, String rating, String comment, String date, String user_location, String type) {
        this.product_name = product_name;
        this.category_name = category_name;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.user_location = user_location;
        this.type = type;
    }
}
