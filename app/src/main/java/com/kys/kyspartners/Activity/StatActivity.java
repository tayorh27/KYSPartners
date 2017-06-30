package com.kys.kyspartners.Activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.kys.kyspartners.Fragments.CategoryFragmentTab;
import com.kys.kyspartners.Fragments.ExcelFragmentTab;
import com.kys.kyspartners.Fragments.LocationFragmentTab;
import com.kys.kyspartners.Fragments.ManualFragmentTab;
import com.kys.kyspartners.Fragments.ProductFragmentTab;
import com.kys.kyspartners.Fragments.ViewFragmentTab;
import com.kys.kyspartners.R;
import com.ogaclejapan.smarttablayout.utils.v13.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v13.FragmentPagerItems;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StatActivity extends AppCompatActivity {

    FragmentPagerItemAdapter adapter;
    ViewPager viewPager;
    NavigationTabBar navigationTabBar;
    ArrayList<NavigationTabBar.Model> models = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new FragmentPagerItemAdapter(
                getFragmentManager(), FragmentPagerItems.with(this)
                .add("Views", ViewFragmentTab.class)
                .add("Products", ProductFragmentTab.class)
                .add("Category", CategoryFragmentTab.class)
                .add("Location", LocationFragmentTab.class)
                .create());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        String[] colors = getResources().getStringArray(R.array.colors);

        navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb);
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_visibility_black_24dp),
                        Color.parseColor(colors[0])
                ).title("Views")
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_visibility_white_24dp))
                        .badgeTitle("VIE")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_shopping_basket_black_24dp),
                        Color.parseColor(colors[1])
                ).title("Products")
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_shopping_basket_white_24dp))
                        .badgeTitle("PRO")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_menu_black_24dp),
                        Color.parseColor(colors[2])
                ).title("Categories")
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_menu_white_24dp))
                        .badgeTitle("CAT")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_place_black_24dp),
                        Color.parseColor(colors[3])
                ).title("Locations")
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_place_white_24dp))
                        .badgeTitle("LOC")
                        .build()
        );
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);

        navigationTabBar.setTitleMode(NavigationTabBar.TitleMode.ACTIVE);
        navigationTabBar.setBadgeGravity(NavigationTabBar.BadgeGravity.BOTTOM);
        navigationTabBar.setBadgePosition(NavigationTabBar.BadgePosition.CENTER);
        navigationTabBar.setTypeface("avenir_light.ttf");
        navigationTabBar.setIsBadged(true);
        navigationTabBar.setIsTitled(true);
        navigationTabBar.setIsTinted(true);
        navigationTabBar.setIsBadgeUseTypeface(true);
        navigationTabBar.setBadgeBgColor(Color.RED);
        navigationTabBar.setBadgeTitleColor(Color.WHITE);
        navigationTabBar.setIsSwiped(true);
        //navigationTabBar.setBgColor(Color.BLACK);
        navigationTabBar.setBadgeSize(10);
        navigationTabBar.setTitleSize(12);
        navigationTabBar.setIconSizeFraction(0.5f);

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
}
