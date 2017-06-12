package com.kys.kyspartners.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanniAdewale on 21/05/2017.
 */

public class Separation {

    private static Map<String, Integer> customData = new HashMap<>();

    private static int count = 1;

    public static Map<String, Integer> ReturnedData(ArrayList<String> getValues) {

        customData.clear();

        CheckData(getValues);

        return customData;
    }

    private static void CheckData(ArrayList<String> getValues) {

        for (String value : getValues) {
            if (!customData.containsKey(value)) {
                customData.put(value, count);
            } else {
                int getCount = customData.get(value);
                customData.remove(value);
                customData.put(value, (getCount + 1));
            }
        }

    }


}
