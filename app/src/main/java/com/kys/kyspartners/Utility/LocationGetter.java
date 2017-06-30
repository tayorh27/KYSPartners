package com.kys.kyspartners.Utility;


/**
 * Created by sanniAdewale on 30/06/2017.
 */

public class LocationGetter {

    public static String getEachLocation(String location, String filter) {

        String[] locs = location.split(",");

        if (filter.contentEquals("Area")) {
            return (locs.length == 1 ? locs[0] : locs[1]);
        } else if (filter.contentEquals("State")) {
            return (locs.length == 1 ? locs[0] : locs[2]);
        } else if (filter.contentEquals("Country")) {
            return (locs.length == 1 ? locs[0] : locs[3]);
        } else {
            return "unknown";
        }

    }
}
