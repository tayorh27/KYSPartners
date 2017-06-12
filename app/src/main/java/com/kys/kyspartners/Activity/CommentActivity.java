package com.kys.kyspartners.Activity;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.kys.kyspartners.Adapters.RatingAdapter;
import com.kys.kyspartners.Callbacks.RatingCallback;
import com.kys.kyspartners.Information.Comments;
import com.kys.kyspartners.R;
import com.kys.kyspartners.network.GetLogFromServer;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CommentActivity extends AppCompatActivity implements RatingCallback, SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    RatingAdapter adapter;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setColorSchemeColors(ColorTemplate.COLORFUL_COLORS);
        refreshLayout.setOnRefreshListener(this);
        adapter = new RatingAdapter(CommentActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        recyclerView.setAdapter(adapter);
        LoadComments();
    }

    private void LoadComments() {
        GetLogFromServer getLogFromServer = new GetLogFromServer(CommentActivity.this, null, this);
        getLogFromServer.getRatings();
    }

    @Override
    public void onRatingLoaded(ArrayList<Comments> ratingArrayList) {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
        adapter.LoadRecyclerView(ratingArrayList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        LoadComments();
    }
}
