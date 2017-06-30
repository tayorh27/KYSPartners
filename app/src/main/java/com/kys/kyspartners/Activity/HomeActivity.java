package com.kys.kyspartners.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kys.kyspartners.Adapters.HomeAdapter;
import com.kys.kyspartners.Callbacks.LogCallback;
import com.kys.kyspartners.Callbacks.RatingCallback;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.Information.Comments;
import com.kys.kyspartners.Information.Log;
import com.kys.kyspartners.Information.Products;
import com.kys.kyspartners.Information.Shop;
import com.kys.kyspartners.MyApplication;
import com.kys.kyspartners.R;
import com.kys.kyspartners.network.GetLogFromServer;
import com.kys.kyspartners.widget.CanaroTextView;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.zendesk.sdk.feedback.ui.ContactZendeskActivity;
import com.zendesk.sdk.requests.RequestActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, LogCallback, RatingCallback, SwipeRefreshLayout.OnRefreshListener {

    CanaroTextView tv;
    AppData data;
    Shop shop;
    private static final long RIPPLE_DURATION = 250;
    LinearLayout myProduct, addProduct, dataStat, myShop, myProfile, supportCentre, signout;
    RecyclerView recyclerView;
    SwipeSelector swipeSelector;
    GetLogFromServer getLogFromServer;
    TextView tv_view_all, expand_collapse, tv_total;
    SwipeRefreshLayout refreshLayout;
    HomeAdapter adapter;
    RelativeLayout relativeLayoutComment;


    //@BindView(R.id.toolbar)
    Toolbar toolbar;
    //@BindView(R.id.root)
    FrameLayout root;
    //@BindView(R.id.content_hamburger)
    View contentHamburger;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //ButterKnife.bind(HomeActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }
        root = (FrameLayout) findViewById(R.id.root);
        contentHamburger = findViewById(R.id.content_hamburger);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        relativeLayoutComment = (RelativeLayout) findViewById(R.id.relative_comment);
        swipeSelector = (SwipeSelector) findViewById(R.id.swipeSelector);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(ColorTemplate.COLORFUL_COLORS);
        tv_view_all = (TextView) findViewById(R.id.view);
        expand_collapse = (TextView) findViewById(R.id.scroll_up);
        tv_view_all.setOnClickListener(this);


        data = new AppData(HomeActivity.this);
        shop = data.getShop();
        tv = (CanaroTextView) findViewById(R.id.actionBarTitle);
        tv.setText(shop.name);


        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.navigation_menu, null);
        myProduct = (LinearLayout) guillotineMenu.findViewById(R.id.my_product);
        addProduct = (LinearLayout) guillotineMenu.findViewById(R.id.add_product);
        dataStat = (LinearLayout) guillotineMenu.findViewById(R.id.data_stat);
        myShop = (LinearLayout) guillotineMenu.findViewById(R.id.my_shop);
        myProfile = (LinearLayout) guillotineMenu.findViewById(R.id.my_profile);
        supportCentre = (LinearLayout) guillotineMenu.findViewById(R.id.support_centre);
        signout = (LinearLayout) guillotineMenu.findViewById(R.id.sign_out);
        tv_total = (TextView) guillotineMenu.findViewById(R.id.tv_total_product);
        myProduct.setOnClickListener(this);
        addProduct.setOnClickListener(this);
        dataStat.setOnClickListener(this);
        myShop.setOnClickListener(this);
        myProfile.setOnClickListener(this);
        supportCentre.setOnClickListener(this);
        signout.setOnClickListener(this);
        root.addView(guillotineMenu);

        tv_total.setText(total_number_products());

        adapter = new HomeAdapter(HomeActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        recyclerView.setAdapter(adapter);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();
        LoadCommentsAndFeeds();
    }

    private String total_number_products() {
        ArrayList<Products> products = MyApplication.getWritableDatabase().getAllMyProducts();
        int count = 0;
        for (Products pro : products) {
            if (!pro.shop_name.contentEquals("none")) {
                count++;
            }
        }
        return String.valueOf(count);
    }

    private void LoadCommentsAndFeeds() {
        getLogFromServer = new GetLogFromServer(HomeActivity.this, this, this);
        getLogFromServer.getRatings();
        getLogFromServer.getShopLog();
    }

    public void FullScreen(View view) {
        if (relativeLayoutComment.getVisibility() == View.VISIBLE) {
            relativeLayoutComment.setVisibility(View.GONE);
            expand_collapse.setText("collapse");
        } else {
            relativeLayoutComment.setVisibility(View.VISIBLE);
            expand_collapse.setText("expand");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.my_product) {
            startActivity(new Intent(HomeActivity.this, ProductsActivity.class));
        }
        if (id == R.id.add_product) {
            Intent intent = new Intent(HomeActivity.this, AddProductActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("home", "from_home");
            intent.putExtras(bundle);
            startActivity(intent);
        }
        if (id == R.id.view) {
            if (tv_view_all.getText().toString().contentEquals("view all")) {
                startActivity(new Intent(HomeActivity.this, CommentActivity.class));
            }
        }
        if (id == R.id.data_stat) {
            startActivity(new Intent(HomeActivity.this, StatActivity.class));
        }
        if (id == R.id.my_shop) {
            startActivity(new Intent(HomeActivity.this, ShopActivity.class));
        }
        if (id == R.id.my_profile) {
            startActivity(new Intent(HomeActivity.this, AccountActivity.class));
        }
        if (id == R.id.support_centre) {
            // Start ContactZendeskActivity for a standalone way to create requests
            RequestActivity.startActivity(HomeActivity.this, null);
        }
        if (id == R.id.sign_out) {
            new MaterialDialog.Builder(this)
                    .title("Sign Out")
                    .content("Are you sure you want to sign out?")
                    .negativeText("No")
                    .positiveText("Yes")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            MyApplication.getWritableDatabase().deleteAll();
                            data.SignOut();
                            ArrayList<Products> products = MyApplication.getWritableDatabase().getAllMyProducts();
                            if (products.isEmpty()) {
                                Products p = new Products(0, "none", "", "", "", "", "", "", "", 0);
                                ArrayList<Products> c = new ArrayList<>();
                                c.add(p);
                                MyApplication.getWritableDatabase().insertMyProducts(c, false);
                            }
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                            finish();
                        }
                    }).show();
        }
    }

    @Override
    public void onLogCallback(ArrayList<Log> getLog) {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
        adapter.LoadRecyclerView(getLog);
    }

    @Override
    public void onRatingLoaded(ArrayList<Comments> ratingArrayList) {
        if (ratingArrayList.isEmpty()) {
            tv_view_all.setText("no comment");
        } else {
            tv_view_all.setText("view all");
        }
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
        SwipeItem[] swipeItems = new SwipeItem[ratingArrayList.size()];
        int count = 0;
        for (Comments comments : ratingArrayList) {
            SwipeItem swipeItem = new SwipeItem(count, comments.title, comments.comment);
            swipeItems[count] = swipeItem;
            count++;
        }
        swipeSelector.setItems(swipeItems);
    }

    @Override
    public void onRefresh() {
        LoadCommentsAndFeeds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }
}
