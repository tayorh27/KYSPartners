package com.kys.kyspartners.Information;

/**
 * Created by sanniAdewale on 23/05/2017.
 */

public class User {

    public int id;
    public String username, email, password, access_code;

    public User(int id, String username, String email, String password, String access_code) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.access_code = access_code;
    }
}
