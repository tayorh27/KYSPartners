package com.kys.kyspartners.Information;

/**
 * Created by sanniAdewale on 11/05/2017.
 */

public class Shop {

    public int user_id;

    public String logo, name, desc, full_add, city, area, inside_area, phone_number, open, close;

    public Shop(int user_id, String name, String desc, String logo, String fullAddress, String city, String area, String inside_area, String phone_number, String open, String close) {
        this.user_id = user_id;
        this.logo = logo;
        this.name = name;
        this.desc = desc;
        this.full_add = fullAddress;
        this.city = city;
        this.area = area;
        this.inside_area = inside_area;
        this.phone_number = phone_number;
        this.open = open;
        this.close = close;
    }

    public Shop() {

    }
}
