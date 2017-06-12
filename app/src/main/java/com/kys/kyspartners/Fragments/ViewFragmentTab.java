package com.kys.kyspartners.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kys.kyspartners.Callbacks.ViewCallback;
import com.kys.kyspartners.R;
import com.kys.kyspartners.network.GetShopViewsFromServer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sanniAdewale on 21/05/2017.
 */

public class ViewFragmentTab extends Fragment implements ViewCallback {

    TextView clicks, ratings, rCount;
    RatingBar ratingBar;
    private Timer autoUpdate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clicks = (TextView) view.findViewById(R.id.nClicks);
        ratings = (TextView) view.findViewById(R.id.nRating);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        rCount = (TextView) view.findViewById(R.id.rateCount);

        Log.e("onViewCreated", "called here");

    }

    private void GetNumberOfViewsAndRatings() {
        GetShopViewsFromServer getShopViewsFromServer = new GetShopViewsFromServer(getActivity(), this);
        getShopViewsFromServer.getShopViews();
    }

    @Override
    public void onView(int number, int ratingCount, double rating) {
        clicks.setText("" + number);
        ratings.setText("" + rating);
        ratingBar.setRating((float) rating);
        rCount.setText("" + ratingCount);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", "called here");
        autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                GetNumberOfViewsAndRatings();
            }
        }, 0, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        autoUpdate.cancel();
    }
}
