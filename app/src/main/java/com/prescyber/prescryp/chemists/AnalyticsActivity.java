package com.prescyber.prescryp.chemists;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;
import com.prescyber.prescryp.chemists.Misc.CustomMarkerView;
import com.prescyber.prescryp.chemists.Model.DailyOrderItem;
import com.prescyber.prescryp.chemists.SessionManager.UserSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 4);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));



    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DailyFragment extends Fragment {


        public DailyFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        private LineChart delivered_chart, declined_chart;
        private List<DailyOrderItem> deliveredOrderItems, declinedOrderItem;
        private ConstraintLayout delivered_expand_layout, declined_expand_layout;
        private ImageView delivered_expand_more, delivered_expand_less, declined_expand_more, declined_expand_less;
        boolean delivered_expanded = false, declined_expanded = false;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.daily_analytics_fragment, container, false);
            delivered_chart = rootView.findViewById(R.id.delivered_stats);
            declined_chart = rootView.findViewById(R.id.declined_stats);
            delivered_expand_layout = rootView.findViewById(R.id.delivered_expand_layout);
            declined_expand_layout = rootView.findViewById(R.id.declined_expand_layout);
            delivered_expand_more = rootView.findViewById(R.id.delivered_expand_more);
            delivered_expand_less = rootView.findViewById(R.id.delivered_expand_less);
            declined_expand_more = rootView.findViewById(R.id.declined_expand_more);
            declined_expand_less = rootView.findViewById(R.id.declined_expand_less);

            delivered_chart.setVisibility(View.GONE);
            declined_chart.setVisibility(View.GONE);


            deliveredOrderItems = new ArrayList<>();
            declinedOrderItem = new ArrayList<>();
            getData();

            delivered_expand_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!delivered_expanded){
                        delivered_expand_more.setVisibility(View.GONE);
                        delivered_chart.setVisibility(View.VISIBLE);
                        delivered_expand_less.setVisibility(View.VISIBLE);
                        delivered_chart.animateX(2500);
                    }else {
                        delivered_chart.setVisibility(View.GONE);
                        delivered_expand_less.setVisibility(View.GONE);
                        delivered_expand_more.setVisibility(View.VISIBLE);
                    }
                    delivered_expanded = !delivered_expanded;
                }
            });

            declined_expand_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!declined_expanded){
                        declined_expand_more.setVisibility(View.GONE);
                        declined_chart.setVisibility(View.VISIBLE);
                        declined_expand_less.setVisibility(View.VISIBLE);
                        declined_chart.animateX(2500);
                    }else {
                        declined_chart.setVisibility(View.GONE);
                        declined_expand_less.setVisibility(View.GONE);
                        declined_expand_more.setVisibility(View.VISIBLE);
                    }
                    declined_expanded = !declined_expanded;
                }
            });

            return rootView;
        }

        private void getData() {
            UserSessionManager userSessionManager = new UserSessionManager(getContext());
            HashMap<String, String> user = userSessionManager.getUserDetails();
            final String session_mob = user.get(UserSessionManager.KEY_MOB);
            String url =  "http://prescryp.com/prescriptionUpload/getTodaysOrder.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                //converting the string to json array object
                                //JSONArray array = new JSONArray(response);
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                JSONArray jsonArray = jsonObject.getJSONArray("todays_order");

                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                //int length = jsonArray.length();
                                //traversing through all the object
                                if (success.equals("1")){
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        //getting product object from json array
                                        JSONObject orders = jsonArray.getJSONObject(i);

                                        //adding the product to product list
                                        String order_number = orders.getString("order_number");
                                        String delivery_time = orders.getString("delivery_time");
                                        String order_status = orders.getString("order_status");

                                        if (order_status.equalsIgnoreCase("Delivered")){
                                            boolean contains = false;
                                            for (DailyOrderItem orderItem : deliveredOrderItems){
                                                if (orderItem.getOrderNumber().equalsIgnoreCase(order_number)){
                                                    contains = true;
                                                }
                                            }
                                            if (!contains){
                                                deliveredOrderItems.add(new DailyOrderItem(order_number, getTimeFormatChange(delivery_time)));
                                            }
                                        }else if (order_status.equalsIgnoreCase("Declined")){
                                            boolean contains = false;
                                            for (DailyOrderItem orderItem : deliveredOrderItems){
                                                if (orderItem.getOrderNumber().equalsIgnoreCase(order_number)){
                                                    contains = true;
                                                }
                                            }
                                            if (!contains){
                                                declinedOrderItem.add(new DailyOrderItem(order_number, getTimeFormatChange(delivery_time)));
                                            }
                                        }


                                    }

                                    loadDeliveredDataForBar();
                                    loadDeclinedDataForBar();

                                }else if (success.equals("2")){
                                    loadDeliveredDataForBar();
                                    loadDeclinedDataForBar();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile_number", session_mob);
                    return params;
                }
            };

            Volley.newRequestQueue(getContext()).add(stringRequest);
        }

        private void loadDeliveredDataForBar() {
            String time_00 = "00:00:00";
            String time_01 = "01:00:00";
            String time_02 = "02:00:00";
            String time_03 = "03:00:00";
            String time_04 = "04:00:00";
            String time_05 = "05:00:00";
            String time_06 = "06:00:00";
            String time_07 = "07:00:00";
            String time_08 = "08:00:00";
            String time_09 = "09:00:00";
            String time_10 = "10:00:00";
            String time_11 = "11:00:00";
            String time_12 = "12:00:00";
            String time_13 = "13:00:00";
            String time_14 = "14:00:00";
            String time_15 = "15:00:00";
            String time_16 = "16:00:00";
            String time_17 = "17:00:00";
            String time_18 = "18:00:00";
            String time_19 = "19:00:00";
            String time_20 = "20:00:00";
            String time_21 = "21:00:00";
            String time_22 = "22:00:00";
            String time_23 = "23:00:00";
            String time_24 = "24:00:00";

            int order_00_01 = 0;
            int order_01_02 = 0;
            int order_02_03 = 0;
            int order_03_04 = 0;
            int order_04_05 = 0;
            int order_05_06 = 0;
            int order_06_07 = 0;
            int order_07_08 = 0;
            int order_08_09 = 0;
            int order_09_10 = 0;
            int order_10_11 = 0;
            int order_11_12 = 0;
            int order_12_13 = 0;
            int order_13_14 = 0;
            int order_14_15 = 0;
            int order_15_16 = 0;
            int order_16_17 = 0;
            int order_17_18 = 0;
            int order_18_19 = 0;
            int order_19_20 = 0;
            int order_20_21 = 0;
            int order_21_22 = 0;
            int order_22_23 = 0;
            int order_23_24 = 0;

            for (DailyOrderItem item : deliveredOrderItems){
                if (item.getDeliveryTime().compareTo(time_00) > 0 && item.getDeliveryTime().compareTo(time_01) < 0){
                    order_00_01++;
                }else if (item.getDeliveryTime().compareTo(time_01) > 0 && item.getDeliveryTime().compareTo(time_02) < 0){
                    order_01_02++;
                }else if (item.getDeliveryTime().compareTo(time_02) > 0 && item.getDeliveryTime().compareTo(time_03) < 0){
                    order_02_03++;
                }else if (item.getDeliveryTime().compareTo(time_03) > 0 && item.getDeliveryTime().compareTo(time_04) < 0){
                    order_03_04++;
                }else if (item.getDeliveryTime().compareTo(time_04) > 0 && item.getDeliveryTime().compareTo(time_05) < 0){
                    order_04_05++;
                }else if (item.getDeliveryTime().compareTo(time_05) > 0 && item.getDeliveryTime().compareTo(time_06) < 0){
                    order_05_06++;
                }else if (item.getDeliveryTime().compareTo(time_06) > 0 && item.getDeliveryTime().compareTo(time_07) < 0){
                    order_06_07++;
                }else if (item.getDeliveryTime().compareTo(time_07) > 0 && item.getDeliveryTime().compareTo(time_08) < 0){
                    order_07_08++;
                }else if (item.getDeliveryTime().compareTo(time_08) > 0 && item.getDeliveryTime().compareTo(time_09) < 0){
                    order_08_09++;
                }else if (item.getDeliveryTime().compareTo(time_09) > 0 && item.getDeliveryTime().compareTo(time_10) < 0){
                    order_09_10++;
                }else if (item.getDeliveryTime().compareTo(time_10) > 0 && item.getDeliveryTime().compareTo(time_11) < 0){
                    order_10_11++;
                }else if (item.getDeliveryTime().compareTo(time_11) > 0 && item.getDeliveryTime().compareTo(time_12) < 0){
                    order_11_12++;
                }else if (item.getDeliveryTime().compareTo(time_12) > 0 && item.getDeliveryTime().compareTo(time_13) < 0){
                    order_12_13++;
                }else if (item.getDeliveryTime().compareTo(time_13) > 0 && item.getDeliveryTime().compareTo(time_14) < 0){
                    order_13_14++;
                }else if (item.getDeliveryTime().compareTo(time_14) > 0 && item.getDeliveryTime().compareTo(time_15) < 0){
                    order_14_15++;
                }else if (item.getDeliveryTime().compareTo(time_15) > 0 && item.getDeliveryTime().compareTo(time_16) < 0){
                    order_15_16++;
                }else if (item.getDeliveryTime().compareTo(time_16) > 0 && item.getDeliveryTime().compareTo(time_17) < 0){
                    order_16_17++;
                }else if (item.getDeliveryTime().compareTo(time_17) > 0 && item.getDeliveryTime().compareTo(time_18) < 0){
                    order_17_18++;
                }else if (item.getDeliveryTime().compareTo(time_18) > 0 && item.getDeliveryTime().compareTo(time_19) < 0){
                    order_18_19++;
                }else if (item.getDeliveryTime().compareTo(time_19) > 0 && item.getDeliveryTime().compareTo(time_20) < 0){
                    order_19_20++;
                }else if (item.getDeliveryTime().compareTo(time_20) > 0 && item.getDeliveryTime().compareTo(time_21) < 0){
                    order_20_21++;
                }else if (item.getDeliveryTime().compareTo(time_21) > 0 && item.getDeliveryTime().compareTo(time_22) < 0){
                    order_21_22++;
                }else if (item.getDeliveryTime().compareTo(time_22) > 0 && item.getDeliveryTime().compareTo(time_23) < 0){
                    order_22_23++;
                }else if (item.getDeliveryTime().compareTo(time_23) > 0 && item.getDeliveryTime().compareTo(time_24) < 0){
                    order_23_24++;
                }

            }
            List<Entry> values = new ArrayList<>();
            values.add(new Entry(0, order_00_01));
            values.add(new Entry(1, order_01_02));
            values.add(new Entry(3, order_02_03));
            values.add(new Entry(4, order_03_04));
            values.add(new Entry(5, order_04_05));
            values.add(new Entry(6, order_05_06));
            values.add(new Entry(7, order_06_07));
            values.add(new Entry(8, order_07_08));
            values.add(new Entry(9, order_08_09));
            values.add(new Entry(10, order_09_10));
            values.add(new Entry(11, order_10_11));
            values.add(new Entry(12, order_11_12));
            values.add(new Entry(13, order_12_13));
            values.add(new Entry(14, order_13_14));
            values.add(new Entry(15, order_14_15));
            values.add(new Entry(16, order_15_16));
            values.add(new Entry(17, order_16_17));
            values.add(new Entry(18, order_17_18));
            values.add(new Entry(19, order_18_19));
            values.add(new Entry(20, order_19_20));
            values.add(new Entry(21, order_20_21));
            values.add(new Entry(22, order_21_22));
            values.add(new Entry(23, order_22_23));
            values.add(new Entry(24, order_23_24));


            final String[] label = new String[]{"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
                                                "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00",
                                                "23:00", "00:00"};

            LineDataSet dataSet = new LineDataSet(values, "Order Delivered");
            dataSet.setDrawValues(false);
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.themeBlue));



            //****
            // Controlling X axis
            XAxis xAxis = delivered_chart.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //Customizing x axis value

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return label[(int) value];
                }
            };
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            //***
            // Controlling right side of y axis
            YAxis yAxisRight = delivered_chart.getAxisRight();
            yAxisRight.setEnabled(false);

            //***
            // Controlling left side of y axis
            YAxis yAxisLeft = delivered_chart.getAxisLeft();
            yAxisLeft.setGranularity(1f);

            // Setting Data
            LineData data = new LineData(dataSet);
            delivered_chart.setData(data);
            delivered_chart.getLegend().setEnabled(false);
            delivered_chart.getDescription().setEnabled(false);
            delivered_chart.getData().setHighlightEnabled(false);
            delivered_chart.getXAxis().setDrawGridLines(false);
            delivered_chart.getAxisLeft().setDrawGridLines(false);
            delivered_chart.animateX(2500);

            delivered_chart.setHighlightPerTapEnabled(true);
            delivered_chart.setTouchEnabled(true);
            CustomMarkerView mv = new CustomMarkerView(this.getContext(), R.layout.custom_marker_view_layout);
            delivered_chart.setMarker(mv);
            //refresh
            delivered_chart.invalidate();




        }

        private void loadDeclinedDataForBar() {
            String time_00 = "00:00:00";
            String time_01 = "01:00:00";
            String time_02 = "02:00:00";
            String time_03 = "03:00:00";
            String time_04 = "04:00:00";
            String time_05 = "05:00:00";
            String time_06 = "06:00:00";
            String time_07 = "07:00:00";
            String time_08 = "08:00:00";
            String time_09 = "09:00:00";
            String time_10 = "10:00:00";
            String time_11 = "11:00:00";
            String time_12 = "12:00:00";
            String time_13 = "13:00:00";
            String time_14 = "14:00:00";
            String time_15 = "15:00:00";
            String time_16 = "16:00:00";
            String time_17 = "17:00:00";
            String time_18 = "18:00:00";
            String time_19 = "19:00:00";
            String time_20 = "20:00:00";
            String time_21 = "21:00:00";
            String time_22 = "22:00:00";
            String time_23 = "23:00:00";
            String time_24 = "24:00:00";

            int order_00_01 = 0;
            int order_01_02 = 0;
            int order_02_03 = 0;
            int order_03_04 = 0;
            int order_04_05 = 0;
            int order_05_06 = 0;
            int order_06_07 = 0;
            int order_07_08 = 0;
            int order_08_09 = 0;
            int order_09_10 = 0;
            int order_10_11 = 0;
            int order_11_12 = 0;
            int order_12_13 = 0;
            int order_13_14 = 0;
            int order_14_15 = 0;
            int order_15_16 = 0;
            int order_16_17 = 0;
            int order_17_18 = 0;
            int order_18_19 = 0;
            int order_19_20 = 0;
            int order_20_21 = 0;
            int order_21_22 = 0;
            int order_22_23 = 0;
            int order_23_24 = 0;

            for (DailyOrderItem item : declinedOrderItem){
                if (item.getDeliveryTime().compareTo(time_00) > 0 && item.getDeliveryTime().compareTo(time_01) < 0){
                    order_00_01++;
                }else if (item.getDeliveryTime().compareTo(time_01) > 0 && item.getDeliveryTime().compareTo(time_02) < 0){
                    order_01_02++;
                }else if (item.getDeliveryTime().compareTo(time_02) > 0 && item.getDeliveryTime().compareTo(time_03) < 0){
                    order_02_03++;
                }else if (item.getDeliveryTime().compareTo(time_03) > 0 && item.getDeliveryTime().compareTo(time_04) < 0){
                    order_03_04++;
                }else if (item.getDeliveryTime().compareTo(time_04) > 0 && item.getDeliveryTime().compareTo(time_05) < 0){
                    order_04_05++;
                }else if (item.getDeliveryTime().compareTo(time_05) > 0 && item.getDeliveryTime().compareTo(time_06) < 0){
                    order_05_06++;
                }else if (item.getDeliveryTime().compareTo(time_06) > 0 && item.getDeliveryTime().compareTo(time_07) < 0){
                    order_06_07++;
                }else if (item.getDeliveryTime().compareTo(time_07) > 0 && item.getDeliveryTime().compareTo(time_08) < 0){
                    order_07_08++;
                }else if (item.getDeliveryTime().compareTo(time_08) > 0 && item.getDeliveryTime().compareTo(time_09) < 0){
                    order_08_09++;
                }else if (item.getDeliveryTime().compareTo(time_09) > 0 && item.getDeliveryTime().compareTo(time_10) < 0){
                    order_09_10++;
                }else if (item.getDeliveryTime().compareTo(time_10) > 0 && item.getDeliveryTime().compareTo(time_11) < 0){
                    order_10_11++;
                }else if (item.getDeliveryTime().compareTo(time_11) > 0 && item.getDeliveryTime().compareTo(time_12) < 0){
                    order_11_12++;
                }else if (item.getDeliveryTime().compareTo(time_12) > 0 && item.getDeliveryTime().compareTo(time_13) < 0){
                    order_12_13++;
                }else if (item.getDeliveryTime().compareTo(time_13) > 0 && item.getDeliveryTime().compareTo(time_14) < 0){
                    order_13_14++;
                }else if (item.getDeliveryTime().compareTo(time_14) > 0 && item.getDeliveryTime().compareTo(time_15) < 0){
                    order_14_15++;
                }else if (item.getDeliveryTime().compareTo(time_15) > 0 && item.getDeliveryTime().compareTo(time_16) < 0){
                    order_15_16++;
                }else if (item.getDeliveryTime().compareTo(time_16) > 0 && item.getDeliveryTime().compareTo(time_17) < 0){
                    order_16_17++;
                }else if (item.getDeliveryTime().compareTo(time_17) > 0 && item.getDeliveryTime().compareTo(time_18) < 0){
                    order_17_18++;
                }else if (item.getDeliveryTime().compareTo(time_18) > 0 && item.getDeliveryTime().compareTo(time_19) < 0){
                    order_18_19++;
                }else if (item.getDeliveryTime().compareTo(time_19) > 0 && item.getDeliveryTime().compareTo(time_20) < 0){
                    order_19_20++;
                }else if (item.getDeliveryTime().compareTo(time_20) > 0 && item.getDeliveryTime().compareTo(time_21) < 0){
                    order_20_21++;
                }else if (item.getDeliveryTime().compareTo(time_21) > 0 && item.getDeliveryTime().compareTo(time_22) < 0){
                    order_21_22++;
                }else if (item.getDeliveryTime().compareTo(time_22) > 0 && item.getDeliveryTime().compareTo(time_23) < 0){
                    order_22_23++;
                }else if (item.getDeliveryTime().compareTo(time_23) > 0 && item.getDeliveryTime().compareTo(time_24) < 0){
                    order_23_24++;
                }

            }
            List<Entry> values = new ArrayList<>();
            values.add(new Entry(0, order_00_01));
            values.add(new Entry(1, order_01_02));
            values.add(new Entry(3, order_02_03));
            values.add(new Entry(4, order_03_04));
            values.add(new Entry(5, order_04_05));
            values.add(new Entry(6, order_05_06));
            values.add(new Entry(7, order_06_07));
            values.add(new Entry(8, order_07_08));
            values.add(new Entry(9, order_08_09));
            values.add(new Entry(10, order_09_10));
            values.add(new Entry(11, order_10_11));
            values.add(new Entry(12, order_11_12));
            values.add(new Entry(13, order_12_13));
            values.add(new Entry(14, order_13_14));
            values.add(new Entry(15, order_14_15));
            values.add(new Entry(16, order_15_16));
            values.add(new Entry(17, order_16_17));
            values.add(new Entry(18, order_17_18));
            values.add(new Entry(19, order_18_19));
            values.add(new Entry(20, order_19_20));
            values.add(new Entry(21, order_20_21));
            values.add(new Entry(22, order_21_22));
            values.add(new Entry(23, order_22_23));
            values.add(new Entry(24, order_23_24));


            final String[] label = new String[]{"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
                    "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00",
                    "23:00", "00:00"};

            LineDataSet dataSet = new LineDataSet(values, "Order Declined");
            dataSet.setDrawValues(false);
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.themeBlue));



            //****
            // Controlling X axis
            XAxis xAxis = declined_chart.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //Customizing x axis value

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return label[(int) value];
                }
            };
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            //***
            // Controlling right side of y axis
            YAxis yAxisRight = declined_chart.getAxisRight();
            yAxisRight.setEnabled(false);

            //***
            // Controlling left side of y axis
            YAxis yAxisLeft = declined_chart.getAxisLeft();
            yAxisLeft.setGranularity(1f);

            // Setting Data
            LineData data = new LineData(dataSet);
            declined_chart.setData(data);
            declined_chart.getLegend().setEnabled(false);
            declined_chart.getDescription().setEnabled(false);
            declined_chart.getData().setHighlightEnabled(false);
            declined_chart.getXAxis().setDrawGridLines(false);
            declined_chart.getAxisLeft().setDrawGridLines(false);
            declined_chart.animateX(2500);

            declined_chart.setHighlightPerTapEnabled(true);
            declined_chart.setTouchEnabled(true);
            CustomMarkerView mv = new CustomMarkerView(this.getContext(), R.layout.custom_marker_view_layout);
            declined_chart.setMarker(mv);
            //refresh
            declined_chart.invalidate();




        }

        private String getTimeFormatChange(String time){
            DateFormat inputFormat = new SimpleDateFormat("hh:mm:ss aa", Locale.US);
            DateFormat outputFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            String outputDateStr = "";
            try {
                Date new_time = inputFormat.parse(time);
                outputDateStr = outputFormat.format(new_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return outputDateStr;
        }
    }

    public static class WeeklyFragment extends Fragment {


        public WeeklyFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        private LineChart delivered_chart, declined_chart;
        private List<DailyOrderItem> deliveredOrderItems, declinedOrderItem;
        private ConstraintLayout delivered_expand_layout, declined_expand_layout;
        private ImageView delivered_expand_more, delivered_expand_less, declined_expand_more, declined_expand_less;
        boolean delivered_expanded = false, declined_expanded = false;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.weekly_analytics_fragment, container, false);
            delivered_chart = rootView.findViewById(R.id.delivered_stats);
            declined_chart = rootView.findViewById(R.id.declined_stats);
            delivered_expand_layout = rootView.findViewById(R.id.delivered_expand_layout);
            declined_expand_layout = rootView.findViewById(R.id.declined_expand_layout);
            delivered_expand_more = rootView.findViewById(R.id.delivered_expand_more);
            delivered_expand_less = rootView.findViewById(R.id.delivered_expand_less);
            declined_expand_more = rootView.findViewById(R.id.declined_expand_more);
            declined_expand_less = rootView.findViewById(R.id.declined_expand_less);

            delivered_chart.setVisibility(View.GONE);
            declined_chart.setVisibility(View.GONE);


            deliveredOrderItems = new ArrayList<>();
            declinedOrderItem = new ArrayList<>();


            SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);
            Date d = new Date();
            String dayOfTheWeek = sdf.format(d);

            SimpleDateFormat sdf_month = new SimpleDateFormat("yyyy-MM", Locale.US);
            String year_month = sdf_month.format(d);

            SimpleDateFormat sdf_date = new SimpleDateFormat("dd", Locale.US);
            String date = sdf_date.format(d);

            StringBuilder day_list = new StringBuilder();
            int start_date = 0;
            if (dayOfTheWeek.equalsIgnoreCase("Monday")){
                start_date = Integer.valueOf(date) - 0;
            }else if (dayOfTheWeek.equalsIgnoreCase("Tuesday")){
                start_date = Integer.valueOf(date) - 1;
            }else if (dayOfTheWeek.equalsIgnoreCase("Wednesday")){
                start_date = Integer.valueOf(date) - 2;
            }else if (dayOfTheWeek.equalsIgnoreCase("Thursday")){
                start_date = Integer.valueOf(date) - 3;
            }else if (dayOfTheWeek.equalsIgnoreCase("Friday")){
                start_date = Integer.valueOf(date) - 4;
            }else if (dayOfTheWeek.equalsIgnoreCase("Saturday")){
                start_date = Integer.valueOf(date) - 5;
            }else if (dayOfTheWeek.equalsIgnoreCase("Sunday")){
                start_date = Integer.valueOf(date) - 6;
            }

            for (int i = start_date; i<=Integer.valueOf(date); i++){
                day_list.append(year_month).append("-").append(new DecimalFormat("00").format(i)).append(", ");
            }
            String da = day_list.substring(0, day_list.length()-2);

            Log.e("DATE_FOR_WEEK", da);

            getData(da);

            delivered_expand_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!delivered_expanded){
                        delivered_expand_more.setVisibility(View.GONE);
                        delivered_chart.setVisibility(View.VISIBLE);
                        delivered_expand_less.setVisibility(View.VISIBLE);
                        delivered_chart.animateX(2500);
                    }else {
                        delivered_chart.setVisibility(View.GONE);
                        delivered_expand_less.setVisibility(View.GONE);
                        delivered_expand_more.setVisibility(View.VISIBLE);
                    }
                    delivered_expanded = !delivered_expanded;
                }
            });

            declined_expand_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!declined_expanded){
                        declined_expand_more.setVisibility(View.GONE);
                        declined_chart.setVisibility(View.VISIBLE);
                        declined_expand_less.setVisibility(View.VISIBLE);
                        declined_chart.animateX(2500);
                    }else {
                        declined_chart.setVisibility(View.GONE);
                        declined_expand_less.setVisibility(View.GONE);
                        declined_expand_more.setVisibility(View.VISIBLE);
                    }
                    declined_expanded = !declined_expanded;
                }
            });


            return rootView;
        }

        private void getData(final String dates) {
            UserSessionManager userSessionManager = new UserSessionManager(getContext());
            HashMap<String, String> user = userSessionManager.getUserDetails();
            final String session_mob = user.get(UserSessionManager.KEY_MOB);
            String url =  "http://prescryp.com/prescriptionUpload/getWeeklyOrder.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                //converting the string to json array object
                                //JSONArray array = new JSONArray(response);
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                JSONArray jsonArray = jsonObject.getJSONArray("todays_order");

                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                //int length = jsonArray.length();
                                //traversing through all the object
                                if (success.equals("1")){
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        //getting product object from json array
                                        JSONObject orders = jsonArray.getJSONObject(i);

                                        //adding the product to product list
                                        String order_number = orders.getString("order_number");
                                        String delivery_date = orders.getString("delivery_date");
                                        String order_status = orders.getString("order_status");

                                        if (order_status.equalsIgnoreCase("Delivered")){
                                            boolean contains = false;
                                            for (DailyOrderItem orderItem : deliveredOrderItems){
                                                if (orderItem.getOrderNumber().equalsIgnoreCase(order_number)){
                                                    contains = true;
                                                }
                                            }
                                            if (!contains){
                                                deliveredOrderItems.add(new DailyOrderItem(order_number, getDayFormatChange(delivery_date)));
                                            }
                                        }else if (order_status.equalsIgnoreCase("Declined")){
                                            boolean contains = false;
                                            for (DailyOrderItem orderItem : deliveredOrderItems){
                                                if (orderItem.getOrderNumber().equalsIgnoreCase(order_number)){
                                                    contains = true;
                                                }
                                            }
                                            if (!contains){
                                                declinedOrderItem.add(new DailyOrderItem(order_number, getDayFormatChange(delivery_date)));
                                            }
                                        }


                                    }

                                    loadDeliveredDataForBar();
                                    loadDeclinedDataForBar();

                                }else if (success.equals("2")){
                                    loadDeliveredDataForBar();
                                    loadDeclinedDataForBar();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile_number", session_mob);
                    params.put("dates", dates);
                    return params;
                }
            };

            Volley.newRequestQueue(getContext()).add(stringRequest);
        }

        private void loadDeliveredDataForBar() {
            int order_monday = 0;
            int order_tuesday = 0;
            int order_wednesday = 0;
            int order_thursday = 0;
            int order_friday = 0;
            int order_saturday = 0;
            int order_sunday = 0;

            for (DailyOrderItem item : deliveredOrderItems){
                if (item.getDeliveryTime().equalsIgnoreCase("Monday")){
                    order_monday += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("Tuesday")){
                    order_tuesday += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("Wednesday")){
                    order_wednesday += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("Thursday")){
                    order_thursday += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("Friday")){
                    order_friday += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("Satusday")){
                    order_saturday += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("Sunday")){
                    order_sunday += 1;
                }

            }
            List<Entry> values = new ArrayList<>();
            values.add(new Entry(0, order_monday));
            values.add(new Entry(1, order_tuesday));
            values.add(new Entry(2, order_wednesday));
            values.add(new Entry(3, order_thursday));
            values.add(new Entry(4, order_friday));
            values.add(new Entry(5, order_saturday));
            values.add(new Entry(6, order_sunday));

            final String[] label = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Satusday", "Sunday"};

            LineDataSet dataSet = new LineDataSet(values, "Order Delivered");
            dataSet.setDrawValues(false);
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.themeBlue));



            //****
            // Controlling X axis
            XAxis xAxis = delivered_chart.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //Customizing x axis value

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return label[(int) value];
                }
            };
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            //***
            // Controlling right side of y axis
            YAxis yAxisRight = delivered_chart.getAxisRight();
            yAxisRight.setEnabled(false);

            //***
            // Controlling left side of y axis
            YAxis yAxisLeft = delivered_chart.getAxisLeft();
            yAxisLeft.setGranularity(1f);

            // Setting Data
            LineData data = new LineData(dataSet);
            delivered_chart.setData(data);
            delivered_chart.getLegend().setEnabled(false);
            delivered_chart.getDescription().setEnabled(false);
            delivered_chart.getData().setHighlightEnabled(false);
            delivered_chart.getXAxis().setDrawGridLines(false);
            delivered_chart.getAxisLeft().setDrawGridLines(false);
            delivered_chart.animateX(2500);

            delivered_chart.setHighlightPerTapEnabled(true);
            delivered_chart.setTouchEnabled(true);
            CustomMarkerView mv = new CustomMarkerView(this.getContext(), R.layout.custom_marker_view_layout);
            delivered_chart.setMarker(mv);
            //refresh
            delivered_chart.invalidate();




        }


        private void loadDeclinedDataForBar() {


            int order_monday = 0;
            int order_tuesday = 0;
            int order_wednesday = 0;
            int order_thursday = 0;
            int order_friday = 0;
            int order_saturday = 0;
            int order_sunday = 0;

            for (DailyOrderItem item : declinedOrderItem){
                if (item.getDeliveryTime().equalsIgnoreCase("Monday")){
                    order_monday += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("Tuesday")){
                    order_tuesday += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("Wednesday")){
                    order_tuesday += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("Thursday")){
                    order_tuesday += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("Friday")){
                    order_tuesday += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("Satusday")){
                    order_tuesday += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("Sunday")){
                    order_tuesday += 1;
                }

            }
            List<Entry> values = new ArrayList<>();
            values.add(new Entry(0, order_monday));
            values.add(new Entry(1, order_tuesday));
            values.add(new Entry(2, order_wednesday));
            values.add(new Entry(3, order_thursday));
            values.add(new Entry(4, order_friday));
            values.add(new Entry(5, order_saturday));
            values.add(new Entry(6, order_sunday));

            final String[] label = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Satusday", "Sunday"};

            LineDataSet dataSet = new LineDataSet(values, "Order Declined");
            dataSet.setDrawValues(false);
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.themeBlue));



            //****
            // Controlling X axis
            XAxis xAxis = declined_chart.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setEnabled(true);
            //Customizing x axis value

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return label[(int) value];
                }
            };
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            //***
            // Controlling right side of y axis
            YAxis yAxisRight = declined_chart.getAxisRight();
            yAxisRight.setEnabled(false);

            //***
            // Controlling left side of y axis
            YAxis yAxisLeft = declined_chart.getAxisLeft();
            yAxisLeft.setGranularity(1f);

            // Setting Data
            LineData data = new LineData(dataSet);
            declined_chart.setData(data);
            declined_chart.getLegend().setEnabled(false);
            declined_chart.getDescription().setEnabled(false);
            declined_chart.getData().setHighlightEnabled(false);
            declined_chart.getXAxis().setDrawGridLines(false);
            declined_chart.getAxisLeft().setDrawGridLines(false);
            declined_chart.animateX(2500);

            declined_chart.setHighlightPerTapEnabled(true);
            declined_chart.setTouchEnabled(true);
            CustomMarkerView mv = new CustomMarkerView(this.getContext(), R.layout.custom_marker_view_layout);
            declined_chart.setMarker(mv);
            //refresh
            declined_chart.invalidate();




        }

        private String getDayFormatChange(String delivery_date) {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            DateFormat outputFormat = new SimpleDateFormat("EEEE", Locale.US);
            String outputDateStr = "";
            try {
                Date new_date = inputFormat.parse(delivery_date);
                outputDateStr = outputFormat.format(new_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return outputDateStr;
        }
    }

    public static class MonthlyFragment extends Fragment {


        public MonthlyFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        private LineChart delivered_chart, declined_chart;
        private List<DailyOrderItem> deliveredOrderItems, declinedOrderItem;
        private ConstraintLayout delivered_expand_layout, declined_expand_layout;
        private ImageView delivered_expand_more, delivered_expand_less, declined_expand_more, declined_expand_less;
        boolean delivered_expanded = false, declined_expanded = false;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.monthly_analytics_fragment, container, false);
            delivered_chart = rootView.findViewById(R.id.delivered_stats);
            declined_chart = rootView.findViewById(R.id.declined_stats);
            delivered_expand_layout = rootView.findViewById(R.id.delivered_expand_layout);
            declined_expand_layout = rootView.findViewById(R.id.declined_expand_layout);
            delivered_expand_more = rootView.findViewById(R.id.delivered_expand_more);
            delivered_expand_less = rootView.findViewById(R.id.delivered_expand_less);
            declined_expand_more = rootView.findViewById(R.id.declined_expand_more);
            declined_expand_less = rootView.findViewById(R.id.declined_expand_less);

            delivered_chart.setVisibility(View.GONE);
            declined_chart.setVisibility(View.GONE);


            deliveredOrderItems = new ArrayList<>();
            declinedOrderItem = new ArrayList<>();

            SimpleDateFormat sdf_month = new SimpleDateFormat("yyyy-MM", Locale.US);
            String year_month = sdf_month.format(new Date());

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            StringBuilder day_list = new StringBuilder();

            for (int i = 1; i<=maxDay; i++){
                day_list.append(year_month).append("-").append(new DecimalFormat("00").format(i)).append(", ");
            }
            String da = day_list.substring(0, day_list.length()-2);

            Log.e("DATE_FOR_MONTH", da);

            getData(da);

            delivered_expand_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!delivered_expanded){
                        delivered_expand_more.setVisibility(View.GONE);
                        delivered_chart.setVisibility(View.VISIBLE);
                        delivered_expand_less.setVisibility(View.VISIBLE);
                        delivered_chart.animateX(2500);
                    }else {
                        delivered_chart.setVisibility(View.GONE);
                        delivered_expand_less.setVisibility(View.GONE);
                        delivered_expand_more.setVisibility(View.VISIBLE);
                    }
                    delivered_expanded = !delivered_expanded;
                }
            });

            declined_expand_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!declined_expanded){
                        declined_expand_more.setVisibility(View.GONE);
                        declined_chart.setVisibility(View.VISIBLE);
                        declined_expand_less.setVisibility(View.VISIBLE);
                        declined_chart.animateX(2500);
                    }else {
                        declined_chart.setVisibility(View.GONE);
                        declined_expand_less.setVisibility(View.GONE);
                        declined_expand_more.setVisibility(View.VISIBLE);
                    }
                    declined_expanded = !declined_expanded;
                }
            });

            return rootView;
        }

        private void getData(final String dates) {
            UserSessionManager userSessionManager = new UserSessionManager(getContext());
            HashMap<String, String> user = userSessionManager.getUserDetails();
            final String session_mob = user.get(UserSessionManager.KEY_MOB);
            String url =  "http://prescryp.com/prescriptionUpload/getWeeklyOrder.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                //converting the string to json array object
                                //JSONArray array = new JSONArray(response);
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                JSONArray jsonArray = jsonObject.getJSONArray("todays_order");

                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                //int length = jsonArray.length();
                                //traversing through all the object
                                if (success.equals("1")){
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        //getting product object from json array
                                        JSONObject orders = jsonArray.getJSONObject(i);

                                        //adding the product to product list
                                        String order_number = orders.getString("order_number");
                                        String delivery_date = orders.getString("delivery_date");
                                        String order_status = orders.getString("order_status");

                                        if (order_status.equalsIgnoreCase("Delivered")){
                                            boolean contains = false;
                                            for (DailyOrderItem orderItem : deliveredOrderItems){
                                                if (orderItem.getOrderNumber().equalsIgnoreCase(order_number)){
                                                    contains = true;
                                                }
                                            }
                                            if (!contains){
                                                deliveredOrderItems.add(new DailyOrderItem(order_number, getWeekNumber(delivery_date)));
                                            }
                                        }else if (order_status.equalsIgnoreCase("Declined")){
                                            boolean contains = false;
                                            for (DailyOrderItem orderItem : deliveredOrderItems){
                                                if (orderItem.getOrderNumber().equalsIgnoreCase(order_number)){
                                                    contains = true;
                                                }
                                            }
                                            if (!contains){
                                                declinedOrderItem.add(new DailyOrderItem(order_number, getWeekNumber(delivery_date)));
                                            }
                                        }


                                    }

                                    loadDeliveredDataForBar();
                                    loadDeclinedDataForBar();

                                }else if (success.equals("2")){
                                    loadDeliveredDataForBar();
                                    loadDeclinedDataForBar();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile_number", session_mob);
                    params.put("dates", dates);
                    return params;
                }
            };

            Volley.newRequestQueue(getContext()).add(stringRequest);
        }

        private void loadDeliveredDataForBar() {
            int order_week_1 = 0;
            int order_week_2 = 0;
            int order_week_3 = 0;
            int order_week_4 = 0;
            int order_week_5 = 0;
            int order_week_6 = 0;

            for (DailyOrderItem item : deliveredOrderItems){
                if (item.getDeliveryTime().equalsIgnoreCase("1")){
                    order_week_1 += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("2")){
                    order_week_2 += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("3")){
                    order_week_3 += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("4")){
                    order_week_4 += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("5")){
                    order_week_5 += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("6")){
                    order_week_6 += 1;
                }

            }
            List<Entry> values = new ArrayList<>();
            values.add(new Entry(0, order_week_1));
            values.add(new Entry(1, order_week_2));
            values.add(new Entry(2, order_week_3));
            values.add(new Entry(3, order_week_4));
            values.add(new Entry(4, order_week_5));
            values.add(new Entry(5, order_week_6));

            final String[] label = new String[]{"Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6"};

            LineDataSet dataSet = new LineDataSet(values, "Order Delivered");
            dataSet.setDrawValues(false);
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.themeBlue));



            //****
            // Controlling X axis
            XAxis xAxis = delivered_chart.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //Customizing x axis value

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return label[(int) value];
                }
            };
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            //***
            // Controlling right side of y axis
            YAxis yAxisRight = delivered_chart.getAxisRight();
            yAxisRight.setEnabled(false);

            //***
            // Controlling left side of y axis
            YAxis yAxisLeft = delivered_chart.getAxisLeft();
            yAxisLeft.setGranularity(1f);

            // Setting Data
            LineData data = new LineData(dataSet);
            delivered_chart.setData(data);
            delivered_chart.getLegend().setEnabled(false);
            delivered_chart.getDescription().setEnabled(false);
            delivered_chart.getData().setHighlightEnabled(false);
            delivered_chart.getXAxis().setDrawGridLines(false);
            delivered_chart.getAxisLeft().setDrawGridLines(false);
            delivered_chart.animateX(2500);

            delivered_chart.setHighlightPerTapEnabled(true);
            delivered_chart.setTouchEnabled(true);
            CustomMarkerView mv = new CustomMarkerView(this.getContext(), R.layout.custom_marker_view_layout);
            delivered_chart.setMarker(mv);
            //refresh
            delivered_chart.invalidate();




        }


        private void loadDeclinedDataForBar() {


            int order_week_1 = 0;
            int order_week_2 = 0;
            int order_week_3 = 0;
            int order_week_4 = 0;
            int order_week_5 = 0;
            int order_week_6 = 0;


            for (DailyOrderItem item : declinedOrderItem){
                if (item.getDeliveryTime().equalsIgnoreCase("1")){
                    order_week_1 += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("2")){
                    order_week_2 += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("3")){
                    order_week_3 += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("4")){
                    order_week_4 += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("5")){
                    order_week_5 += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("6")){
                    order_week_6 += 1;
                }

            }
            List<Entry> values = new ArrayList<>();
            values.add(new Entry(0, order_week_1));
            values.add(new Entry(1, order_week_2));
            values.add(new Entry(2, order_week_3));
            values.add(new Entry(3, order_week_4));
            values.add(new Entry(4, order_week_5));
            values.add(new Entry(5, order_week_6));

            final String[] label = new String[]{"Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6"};

            LineDataSet dataSet = new LineDataSet(values, "Order Declined");
            dataSet.setDrawValues(false);
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.themeBlue));



            //****
            // Controlling X axis
            XAxis xAxis = declined_chart.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setEnabled(true);
            //Customizing x axis value

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return label[(int) value];
                }
            };
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            //***
            // Controlling right side of y axis
            YAxis yAxisRight = declined_chart.getAxisRight();
            yAxisRight.setEnabled(false);

            //***
            // Controlling left side of y axis
            YAxis yAxisLeft = declined_chart.getAxisLeft();
            yAxisLeft.setGranularity(1f);

            // Setting Data
            LineData data = new LineData(dataSet);
            declined_chart.setData(data);
            declined_chart.getLegend().setEnabled(false);
            declined_chart.getDescription().setEnabled(false);
            declined_chart.getData().setHighlightEnabled(false);
            declined_chart.getXAxis().setDrawGridLines(false);
            declined_chart.getAxisLeft().setDrawGridLines(false);
            declined_chart.animateX(2500);

            declined_chart.setHighlightPerTapEnabled(true);
            declined_chart.setTouchEnabled(true);
            CustomMarkerView mv = new CustomMarkerView(this.getContext(), R.layout.custom_marker_view_layout);
            declined_chart.setMarker(mv);
            //refresh
            declined_chart.invalidate();




        }


        private String getWeekNumber(String delivery_date) {

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String wk_number = "";
            try {
                Date date = df.parse(delivery_date);
                Calendar ca1 = Calendar.getInstance();
                ca1.setTime(date);
                ca1.setMinimalDaysInFirstWeek(1);
                wk_number = String.valueOf(ca1.get(Calendar.WEEK_OF_MONTH));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return wk_number;

        }
    }

    public static class YearlyFragment extends Fragment {


        public YearlyFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        private LineChart delivered_chart, declined_chart;
        private List<DailyOrderItem> deliveredOrderItems, declinedOrderItem;
        private ConstraintLayout delivered_expand_layout, declined_expand_layout;
        private ImageView delivered_expand_more, delivered_expand_less, declined_expand_more, declined_expand_less;
        boolean delivered_expanded = false, declined_expanded = false;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.yearly_analytics_fragment, container, false);
            delivered_chart = rootView.findViewById(R.id.delivered_stats);
            declined_chart = rootView.findViewById(R.id.declined_stats);
            delivered_expand_layout = rootView.findViewById(R.id.delivered_expand_layout);
            declined_expand_layout = rootView.findViewById(R.id.declined_expand_layout);
            delivered_expand_more = rootView.findViewById(R.id.delivered_expand_more);
            delivered_expand_less = rootView.findViewById(R.id.delivered_expand_less);
            declined_expand_more = rootView.findViewById(R.id.declined_expand_more);
            declined_expand_less = rootView.findViewById(R.id.declined_expand_less);

            delivered_chart.setVisibility(View.GONE);
            declined_chart.setVisibility(View.GONE);


            deliveredOrderItems = new ArrayList<>();
            declinedOrderItem = new ArrayList<>();

            SimpleDateFormat sdf_month = new SimpleDateFormat("yyyy", Locale.US);
            String year = sdf_month.format(new Date());

            getData(year);

            delivered_expand_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!delivered_expanded){
                        delivered_expand_more.setVisibility(View.GONE);
                        delivered_chart.setVisibility(View.VISIBLE);
                        delivered_expand_less.setVisibility(View.VISIBLE);
                        delivered_chart.animateX(2500);
                    }else {
                        delivered_chart.setVisibility(View.GONE);
                        delivered_expand_less.setVisibility(View.GONE);
                        delivered_expand_more.setVisibility(View.VISIBLE);
                    }
                    delivered_expanded = !delivered_expanded;
                }
            });

            declined_expand_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!declined_expanded){
                        declined_expand_more.setVisibility(View.GONE);
                        declined_chart.setVisibility(View.VISIBLE);
                        declined_expand_less.setVisibility(View.VISIBLE);
                        declined_chart.animateX(2500);
                    }else {
                        declined_chart.setVisibility(View.GONE);
                        declined_expand_less.setVisibility(View.GONE);
                        declined_expand_more.setVisibility(View.VISIBLE);
                    }
                    declined_expanded = !declined_expanded;
                }
            });


            return rootView;
        }

        private void getData(final String year) {
            UserSessionManager userSessionManager = new UserSessionManager(getContext());
            HashMap<String, String> user = userSessionManager.getUserDetails();
            final String session_mob = user.get(UserSessionManager.KEY_MOB);
            String url =  "http://prescryp.com/prescriptionUpload/getYearlyOrder.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                //converting the string to json array object
                                //JSONArray array = new JSONArray(response);
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                JSONArray jsonArray = jsonObject.getJSONArray("todays_order");

                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                //int length = jsonArray.length();
                                //traversing through all the object
                                if (success.equals("1")){
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        //getting product object from json array
                                        JSONObject orders = jsonArray.getJSONObject(i);

                                        //adding the product to product list
                                        String order_number = orders.getString("order_number");
                                        String delivery_date = orders.getString("delivery_date");
                                        String order_status = orders.getString("order_status");

                                        if (order_status.equalsIgnoreCase("Delivered")){
                                            boolean contains = false;
                                            for (DailyOrderItem orderItem : deliveredOrderItems){
                                                if (orderItem.getOrderNumber().equalsIgnoreCase(order_number)){
                                                    contains = true;
                                                }
                                            }
                                            if (!contains){
                                                deliveredOrderItems.add(new DailyOrderItem(order_number, getMonthNumber(delivery_date)));
                                            }
                                        }else if (order_status.equalsIgnoreCase("Declined")){
                                            boolean contains = false;
                                            for (DailyOrderItem orderItem : deliveredOrderItems){
                                                if (orderItem.getOrderNumber().equalsIgnoreCase(order_number)){
                                                    contains = true;
                                                }
                                            }
                                            if (!contains){
                                                declinedOrderItem.add(new DailyOrderItem(order_number, getMonthNumber(delivery_date)));
                                            }
                                        }


                                    }

                                    loadDeliveredDataForBar();
                                    loadDeclinedDataForBar();

                                }else if (success.equals("2")){
                                    loadDeliveredDataForBar();
                                    loadDeclinedDataForBar();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("mobile_number", session_mob);
                    params.put("year", year);
                    return params;
                }
            };

            Volley.newRequestQueue(getContext()).add(stringRequest);
        }

        private void loadDeliveredDataForBar() {
            int order_jan = 0;
            int order_feb = 0;
            int order_mar = 0;
            int order_apr = 0;
            int order_may = 0;
            int order_jun = 0;
            int order_jul = 0;
            int order_aug = 0;
            int order_sep = 0;
            int order_oct = 0;
            int order_nov = 0;
            int order_dec = 0;

            for (DailyOrderItem item : deliveredOrderItems){
                if (item.getDeliveryTime().equalsIgnoreCase("01")){
                    order_jan += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("02")){
                    order_feb += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("03")){
                    order_mar += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("04")){
                    order_apr += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("05")){
                    order_may += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("06")){
                    order_jun += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("07")){
                    order_jul += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("08")){
                    order_aug += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("09")){
                    order_sep += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("10")){
                    order_oct += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("11")){
                    order_nov += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("12")){
                    order_dec += 1;
                }

            }
            List<Entry> values = new ArrayList<>();
            values.add(new Entry(0, order_jan));
            values.add(new Entry(1, order_feb));
            values.add(new Entry(2, order_mar));
            values.add(new Entry(3, order_apr));
            values.add(new Entry(4, order_may));
            values.add(new Entry(5, order_jun));
            values.add(new Entry(6, order_jul));
            values.add(new Entry(7, order_aug));
            values.add(new Entry(8, order_sep));
            values.add(new Entry(9, order_oct));
            values.add(new Entry(10, order_nov));
            values.add(new Entry(11, order_dec));

            final String[] label = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            LineDataSet dataSet = new LineDataSet(values, "Order Delivered");
            dataSet.setDrawValues(false);
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.themeBlue));



            //****
            // Controlling X axis
            XAxis xAxis = delivered_chart.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //Customizing x axis value

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return label[(int) value];
                }
            };
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            //***
            // Controlling right side of y axis
            YAxis yAxisRight = delivered_chart.getAxisRight();
            yAxisRight.setEnabled(false);

            //***
            // Controlling left side of y axis
            YAxis yAxisLeft = delivered_chart.getAxisLeft();
            yAxisLeft.setGranularity(1f);

            // Setting Data
            LineData data = new LineData(dataSet);
            delivered_chart.setData(data);
            delivered_chart.getLegend().setEnabled(false);
            delivered_chart.getDescription().setEnabled(false);
            delivered_chart.getData().setHighlightEnabled(false);
            delivered_chart.getXAxis().setDrawGridLines(false);
            delivered_chart.getAxisLeft().setDrawGridLines(false);
            delivered_chart.animateX(2500);

            delivered_chart.setHighlightPerTapEnabled(true);
            delivered_chart.setTouchEnabled(true);
            CustomMarkerView mv = new CustomMarkerView(this.getContext(), R.layout.custom_marker_view_layout);
            delivered_chart.setMarker(mv);
            //refresh
            delivered_chart.invalidate();




        }


        private void loadDeclinedDataForBar() {


            int order_jan = 0;
            int order_feb = 0;
            int order_mar = 0;
            int order_apr = 0;
            int order_may = 0;
            int order_jun = 0;
            int order_jul = 0;
            int order_aug = 0;
            int order_sep = 0;
            int order_oct = 0;
            int order_nov = 0;
            int order_dec = 0;

            for (DailyOrderItem item : declinedOrderItem){
                if (item.getDeliveryTime().equalsIgnoreCase("01")){
                    order_jan += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("02")){
                    order_feb += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("03")){
                    order_mar += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("04")){
                    order_apr += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("05")){
                    order_may += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("06")){
                    order_jun += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("07")){
                    order_jul += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("08")){
                    order_aug += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("09")){
                    order_sep += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("10")){
                    order_oct += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("11")){
                    order_nov += 1;
                }else if (item.getDeliveryTime().equalsIgnoreCase("12")){
                    order_dec += 1;
                }

            }
            List<Entry> values = new ArrayList<>();
            values.add(new Entry(0, order_jan));
            values.add(new Entry(1, order_feb));
            values.add(new Entry(2, order_mar));
            values.add(new Entry(3, order_apr));
            values.add(new Entry(4, order_may));
            values.add(new Entry(5, order_jun));
            values.add(new Entry(6, order_jul));
            values.add(new Entry(7, order_aug));
            values.add(new Entry(8, order_sep));
            values.add(new Entry(9, order_oct));
            values.add(new Entry(10, order_nov));
            values.add(new Entry(11, order_dec));

            final String[] label = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            LineDataSet dataSet = new LineDataSet(values, "Order Declined");
            dataSet.setDrawValues(false);
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.themeBlue));



            //****
            // Controlling X axis
            XAxis xAxis = declined_chart.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setEnabled(true);
            //Customizing x axis value

            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return label[(int) value];
                }
            };
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

            //***
            // Controlling right side of y axis
            YAxis yAxisRight = declined_chart.getAxisRight();
            yAxisRight.setEnabled(false);

            //***
            // Controlling left side of y axis
            YAxis yAxisLeft = declined_chart.getAxisLeft();
            yAxisLeft.setGranularity(1f);

            // Setting Data
            LineData data = new LineData(dataSet);
            declined_chart.setData(data);
            declined_chart.getLegend().setEnabled(false);
            declined_chart.getDescription().setEnabled(false);
            declined_chart.getData().setHighlightEnabled(false);
            declined_chart.getXAxis().setDrawGridLines(false);
            declined_chart.getAxisLeft().setDrawGridLines(false);
            declined_chart.animateX(2500);

            declined_chart.setHighlightPerTapEnabled(true);
            declined_chart.setTouchEnabled(true);
            CustomMarkerView mv = new CustomMarkerView(this.getContext(), R.layout.custom_marker_view_layout);
            declined_chart.setMarker(mv);
            //refresh
            declined_chart.invalidate();




        }

        private String getMonthNumber(String delivery_date) {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            DateFormat outputFormat = new SimpleDateFormat("MM", Locale.US);
            String outputDateStr = "";
            try {
                Date new_date = inputFormat.parse(delivery_date);
                outputDateStr = outputFormat.format(new_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return outputDateStr;
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        int tabCount;

        public SectionsPagerAdapter(FragmentManager fm, int tabCount){
            super(fm);
            this.tabCount=tabCount;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0 :
                    return new DailyFragment();
                case 1 :
                    return new WeeklyFragment();
                case 2 :
                    return new MonthlyFragment();
                case 3 :
                    return new YearlyFragment();

                default: return null;
            }
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }
}
