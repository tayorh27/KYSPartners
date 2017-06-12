package com.kys.kyspartners;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sanniAdewale on 11/05/2017.
 */

public class AppConfig {

    public static String WEB_URL = "http://www.tereso.ng/knowyourshop/partners/";
    public static String WEB_URL2 = "http://www.tereso.ng/knowyourshop/";

    public static String GET_LOCATION_FROM_SERVER = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    public static String GET_DISTANCE_AND_DURATION = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
    public static String GET_DISTANCE_AND_DURATION_DIRECTION = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    public static String GET_NEARBY_PLACES = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";

    public static String API_KEY_FOR_DISTANCE_DURATION = "AIzaSyCp0vrFKhLhFcq8lyc2Z6Zf6UFeZnsZcnY";

    public static String USERNAME = "gisanrinadetayo@gmail.com";
    public static String PASSKEY = "gafarmariam1234$";

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.WEB_URL2)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
