package com.prescyber.prescryp.chemists;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.prescyber.prescryp.chemists.Adapter.StoreManagementMedicineAdapter;
import com.prescyber.prescryp.chemists.Adapter.StoreSearchedMedicineAdapter;
import com.prescyber.prescryp.chemists.Interface.OnBottomReachedListener;
import com.prescyber.prescryp.chemists.Interface.OnNotReachedBottomListener;
import com.prescyber.prescryp.chemists.Model.StoreMedicineItem;
import com.prescyber.prescryp.chemists.SessionManager.UserSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class StockManagementActivity extends AppCompatActivity {

    private RecyclerView medicineShow;
    private int max_id = 0;
    private String session_mob;
    private StoreManagementMedicineAdapter adapter;
    private List<StoreMedicineItem> medicineItemList;
    private CardView loadMore;
    private ProgressBar loadingLoadMore;
    private Animation downToUp, upToDown;
    private int prev_list_size = 0;
    private MaterialSearchView searchView;
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_management);
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

        final SharedPreferences sp = getSharedPreferences("store_max_id", Activity.MODE_PRIVATE);
        max_id = sp.getInt("max_id_key", 0);

        medicineShow = findViewById(R.id.medicineShow);
        loadMore = findViewById(R.id.load_more);
        loadingLoadMore = findViewById(R.id.loading_load_more);
        searchView = findViewById(R.id.searchStockMedicine);


        medicineItemList = new ArrayList<>();

        downToUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        upToDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        getMedView();


        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev_list_size = medicineItemList.size();
                if (prev_list_size > 500){
                    medicineItemList.clear();
                }
                getMedView();
                loadMore.setVisibility(View.INVISIBLE);
                loadingLoadMore.setVisibility(View.VISIBLE);
            }
        });


        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                //Do some magic
                if (newText.length() > 0){
                    //Toast.makeText(getApplicationContext(), s.toString(), Toast.LENGTH_SHORT).show();
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // do your actual work here
                            StockManagementActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    medicineItemList.clear();
                                    getSearchedMedView(newText.toUpperCase());
                                }
                            });

                        }
                    }, 600);

                }else if (newText.matches("")){
                    //Toast.makeText(getApplicationContext(), "Empty", Toast.LENGTH_SHORT).show();
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            StockManagementActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    max_id = sp.getInt("max_id_key", 0);
                                    medicineItemList.clear();
                                    getMedView();
                                }
                            });

                        }
                    }, 600);

                }
                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                max_id = sp.getInt("max_id_key", 0);
                medicineItemList.clear();
                getMedView();
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    private void getSearchedMedView(final String med_name) {
        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = userSessionManager.getUserDetails();
        session_mob = user.get(UserSessionManager.KEY_MOB);
        String url =  "http://prescryp.com/prescriptionUpload/getSearchedStoreMed.php";
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
                            JSONArray jsonArray = jsonObject.getJSONArray("medicines");

                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            //int length = jsonArray.length();
                            //traversing through all the object
                            if (success.equals("1")){
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //getting product object from json array
                                    JSONObject product = jsonArray.getJSONObject(i);

                                    //adding the product to product list
                                    if (product.getString("medicine_pack").contains("INR")){
                                        String[] packaging_list = product.getString("medicine_pack").split(",");
                                        for (String packaging : packaging_list){
                                            Boolean contains = false;
                                            for (StoreMedicineItem medicineItem : medicineItemList){
                                                if (medicineItem.getMedicineName().equalsIgnoreCase(product.getString("medicine_name"))){
                                                    contains = true;
                                                }
                                            }
                                            if (!contains){
                                                medicineItemList.add(new StoreMedicineItem(product.getString("id"),
                                                        product.getString("medicine_name"), product.getString("medicine_manu"),
                                                        product.getString("medicine_form"), packaging, 0, "NO",
                                                        "ADD"));
                                            }

                                        }
                                    }


                                }


                                StoreSearchedMedicineAdapter adapter = new StoreSearchedMedicineAdapter(medicineItemList, StockManagementActivity.this);
                                medicineShow.setAdapter(adapter);
                                medicineShow.setLayoutManager(new LinearLayoutManager(StockManagementActivity.this));





                            }else if (success.equals("2")){
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                params.put("medicine_name", med_name);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stock_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    public void getMedView(){
        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = userSessionManager.getUserDetails();
        session_mob = user.get(UserSessionManager.KEY_MOB);
        String url =  "http://prescryp.com/prescriptionUpload/getMedToStore.php";
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
                            JSONArray jsonArray = jsonObject.getJSONArray("medicines");

                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            //int length = jsonArray.length();
                            //traversing through all the object
                            if (success.equals("1")){
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //getting product object from json array
                                    JSONObject product = jsonArray.getJSONObject(i);

                                    //adding the product to product list
                                    if (product.getString("medicine_pack").contains("INR")){
                                        String[] packaging_list = product.getString("medicine_pack").split(",");
                                        for (String packaging : packaging_list){
                                            medicineItemList.add(new StoreMedicineItem(product.getString("id"),
                                                    product.getString("medicine_name"), product.getString("medicine_manu"),
                                                    product.getString("medicine_form"), packaging, 0, "NO",
                                                    "ADD"));
                                        }
                                    }

                                    if (Integer.valueOf(product.getString("id")) > max_id){
                                        max_id = Integer.valueOf(product.getString("id"));
                                    }

                                }

                                adapter = new StoreManagementMedicineAdapter(medicineItemList, StockManagementActivity.this);
                                medicineShow.setAdapter(adapter);
                                medicineShow.setLayoutManager(new LinearLayoutManager(StockManagementActivity.this));
                                medicineShow.scrollToPosition(prev_list_size - 1);
                                adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                                    @Override
                                    public void onBottomReached(int position) {
                                        //your code goes here
                                        loadMore.setVisibility(View.VISIBLE);
                                        loadMore.setAnimation(downToUp);
                                    }
                                });

                                adapter.setOnNotReachedBottomListener(new OnNotReachedBottomListener(){

                                    @Override
                                    public void onNotReachedBottom(int position) {
                                        loadMore.setVisibility(View.GONE);
                                        loadMore.setAnimation(upToDown);
                                    }
                                });

                                loadingLoadMore.setVisibility(View.INVISIBLE);





                            }else if (success.equals("2")){
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                params.put("sent_id", String.valueOf(max_id));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);


    }
}
