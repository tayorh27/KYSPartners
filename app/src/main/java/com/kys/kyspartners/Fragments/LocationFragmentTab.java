package com.kys.kyspartners.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kys.kyspartners.Callbacks.LogTypeCallback;
import com.kys.kyspartners.R;
import com.kys.kyspartners.Utility.ContextMenuDialogFragmentNoSupport;
import com.kys.kyspartners.Utility.Separation;
import com.kys.kyspartners.network.GetLogType;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sanniAdewale on 30/06/2017.
 */

public class LocationFragmentTab extends Fragment implements LogTypeCallback, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, OnMenuItemClickListener {
    BarChart barChart;
    SwipeRefreshLayout refreshLayout;
    LayoutInflater inflater1;
    View view1;
    ListView listView;
    ArrayList<String> listArray = new ArrayList<>();
    ArrayList<String> listArray2 = new ArrayList<>();
    BottomSheetDialog dialog;
    private ContextMenuDialogFragmentNoSupport mMenuDialogFragment;
    String filter = "Area";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initMenuFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view1 = inflater1.inflate(R.layout.list_layout, container, false);
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChart = (BarChart) view.findViewById(R.id.chart);
        listView = (ListView) view1.findViewById(R.id.listView);

        barChart.setDescription("Each location views chart");

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setColorSchemeColors(ColorTemplate.COLORFUL_COLORS);
        refreshLayout.setOnRefreshListener(this);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);

        YAxis yAxis1 = barChart.getAxisRight();
        yAxis1.setTextColor(Color.WHITE);
        BuildBottomSheet();
        GetTypeForChart();
    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(true);
        menuParams.setAnimationDelay(100);
        menuParams.setAnimationDuration(300);
        mMenuDialogFragment = ContextMenuDialogFragmentNoSupport.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        //mMenuDialogFragment.setItemLongClickListener(this);
    }

    private List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject area = new MenuObject("Filter by Area");
        area.setResource(R.drawable.ic_domain_black_24dp);
        area.setDividerColor(R.color.colorAccent);
        area.setBgColor(R.color.backgrounds);

        MenuObject state = new MenuObject("Filter by State");
        state.setResource(R.drawable.ic_location_city_black_24dp);
        state.setDividerColor(R.color.colorAccent);
        state.setBgColor(R.color.backgrounds);

        MenuObject country = new MenuObject("Filter by Country");
        country.setResource(R.drawable.ic_language_black_24dp);
        country.setDividerColor(R.color.colorAccent);
        country.setBgColor(R.color.backgrounds);

        menuObjects.add(area);
        menuObjects.add(state);
        menuObjects.add(country);
        return menuObjects;
    }

    private void GetTypeForChart() {
        GetLogType getLogType = new GetLogType(getActivity(), this);
        getLogType.getShopLogLocation(filter);
    }

    @Override
    public void onLogType(ArrayList<String> values) {

        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
        Map<String, Integer> _value = Separation.ReturnedData(values);

        BarData data = new BarData(getXAxisValues(_value), getDataSet(_value));
        data.setValueTextColor(getResources().getColor(R.color.colorAccent));
        barChart.setData(data);
        barChart.animateXY(2000, 2000);
        barChart.invalidate();

        ArrayList<String> dataArray = new ArrayList<>();
        for (int i = 0; i < listArray.size(); i++) {
            dataArray.add(listArray.get(i) + "\n" + listArray2.get(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataArray);
        listView.setAdapter(adapter);
    }

    private ArrayList<BarDataSet> getDataSet(Map<String, Integer> _value) {
        listArray2.clear();
        ArrayList<BarDataSet> dataSets;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();

        Collection<Integer> pro_values = _value.values();
        int count = 0;
        for (Integer vl : pro_values) {

            BarEntry v1e1 = new BarEntry(vl, count);
            valueSet1.add(v1e1);
            listArray2.add(vl + "");
            count++;
        }

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Product");
        barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        return dataSets;
    }

    private ArrayList<String> getXAxisValues(Map<String, Integer> _value) {
        listArray.clear();
        ArrayList<String> xAxis = new ArrayList<>();

        Set<String> keys = _value.keySet();

        for (String st : keys) {
            if (st.length() > 2) {
                xAxis.add(st.substring(0, 3));
            } else {
                xAxis.add(st);
            }
            listArray.add(st);
        }

        return xAxis;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {
        GetTypeForChart();
    }

    private void ShowBottomSheet() {
        if (listArray.isEmpty()) {
            Toast.makeText(getActivity(), "No chart available yet", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.frag_loc, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_bottom) {
            ShowBottomSheet();
        }
        if (id == R.id.context_menu) {
            if (getFragmentManager().findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                mMenuDialogFragment.show(getFragmentManager(), ContextMenuDialogFragment.TAG);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void BuildBottomSheet() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 640);
        dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(view1, layoutParams);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }



    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch (position){
            case 0:
                filter = "Area";
                break;
            case 1:
                filter = "State";
                break;
            case 2:
                filter = "Country";
                break;
        }
        GetTypeForChart();
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        }
    }
}
