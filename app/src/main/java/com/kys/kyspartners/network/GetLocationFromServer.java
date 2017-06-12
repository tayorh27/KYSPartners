package com.kys.kyspartners.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Callbacks.LocationCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sanniAdewale on 11/05/2017.
 */

public class GetLocationFromServer {

    public Context context;
    public VolleySingleton volleySingleton;
    public RequestQueue requestQueue;
    public LocationCallback locationCallback;
    public double lat, lng;
    String city, area, inside_area;

    public GetLocationFromServer(Context context, double lat, double lng, LocationCallback locationCallback) {
        this.context = context;
        this.locationCallback = locationCallback;
        this.lat = lat;
        this.lng = lng;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    public void Locate() {
        String url = AppConfig.GET_LOCATION_FROM_SERVER + lat + "," + lng;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    if (status.equals("OK")) {
                        JSONArray array = object.getJSONArray("results");
                        JSONObject object1 = array.getJSONObject(0);
                        JSONArray jsonArray = object1.getJSONArray("address_components");
                        String formatted_address = object1.getString("formatted_address");
                        JSONObject jsonObject_inside_area = jsonArray.getJSONObject(0);
                        JSONArray jsonArray_street_number = jsonObject_inside_area.getJSONArray("types");
                        String types = jsonArray_street_number.getString(0);
                        Log.e("Types", "Type array = " + types);

                        if (types.contentEquals("street_number")) {
                            JSONObject jsonObject_inside_area_new = jsonArray.getJSONObject(1);
                            JSONObject jsonObject_area = jsonArray.getJSONObject(2);
                            JSONObject jsonObject_city = jsonArray.getJSONObject(3);
                            city = jsonObject_city.getString("short_name");
                            area = jsonObject_area.getString("short_name");
                            inside_area = jsonObject_inside_area_new.getString("short_name");
                        } else {
                            JSONObject jsonObject_area = jsonArray.getJSONObject(1);
                            JSONObject jsonObject_city = jsonArray.getJSONObject(2);
                            city = jsonObject_city.getString("short_name");
                            area = jsonObject_area.getString("short_name");
                            inside_area = jsonObject_inside_area.getString("short_name");
                        }

                        if (locationCallback != null) {
                            locationCallback.setLocation(city, area, inside_area, formatted_address);
                        }
                    } else {
                        if (locationCallback != null) {
                            locationCallback.setLocation("", "", "", "");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (locationCallback != null) {
                    locationCallback.setLocation("", "", "", "");
                }
            }
        });
        requestQueue.add(stringRequest);
    }
}
