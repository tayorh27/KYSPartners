package com.kys.kyspartners.Fragments;

import android.app.Fragment;
import android.content.Context;
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
import com.kys.kyspartners.Utility.Separation;
import com.kys.kyspartners.network.GetLogType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by sanniAdewale on 21/05/2017.
 */

public class ProductFragmentTab extends Fragment implements LogTypeCallback, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    BarChart barChart;
    SwipeRefreshLayout refreshLayout;
    LayoutInflater inflater1;
    View view1;
    ListView listView;
    ArrayList<String> listArray = new ArrayList<>();
    BottomSheetDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view1 = inflater1.inflate(R.layout.list_layout, container, false);
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChart = (BarChart) view.findViewById(R.id.chart);
        listView = (ListView) view1.findViewById(R.id.listView);

        barChart.setDescription("Each product views chart");

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

    private void GetTypeForChart() {
        GetLogType getLogType = new GetLogType(getActivity(), this);
        getLogType.getShopLogByType("Product", null);
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listArray);
        listView.setAdapter(adapter);
    }

    private ArrayList<BarDataSet> getDataSet(Map<String, Integer> _value) {
        ArrayList<BarDataSet> dataSets;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();

        Collection<Integer> pro_values = _value.values();
        int count = 0;
        for (Integer vl : pro_values) {

            BarEntry v1e1 = new BarEntry(vl, count);
            valueSet1.add(v1e1);
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
        inflater.inflate(R.menu.frag, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_bottom) {
            ShowBottomSheet();
        }
        return super.onOptionsItemSelected(item);
    }

    private void BuildBottomSheet() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 480);
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
}
