package com.kys.kyspartners.Callbacks;

import com.kys.kyspartners.Information.Comments;

import java.util.ArrayList;

/**
 * Created by sanniAdewale on 21/05/2017.
 */

public interface RatingCallback {
    void onRatingLoaded(ArrayList<Comments> ratingArrayList);
}
