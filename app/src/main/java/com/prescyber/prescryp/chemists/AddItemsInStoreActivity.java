package com.prescyber.prescryp.chemists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.prescyber.prescryp.chemists.Adapter.GeneralStoreItemAdapter;
import com.prescyber.prescryp.chemists.Adapter.GeneralStoreSearchedAdapter;
import com.prescyber.prescryp.chemists.Adapter.StoreManagementMedicineAdapter;
import com.prescyber.prescryp.chemists.Adapter.StoreSearchedMedicineAdapter;
import com.prescyber.prescryp.chemists.Interface.OnBottomReachedListener;
import com.prescyber.prescryp.chemists.Interface.OnNotReachedBottomListener;
import com.prescyber.prescryp.chemists.Misc.Converter;
import com.prescyber.prescryp.chemists.Model.GeneralStoreItem;
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

public class AddItemsInStoreActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private MaterialSearchView medSearchView;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items_in_store);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        mSectionsPagerAdapter = new AddItemsInStoreActivity.SectionsPagerAdapter(getSupportFragmentManager(), 2);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        medSearchView = findViewById(R.id.searchStockMedicine);



        medSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                final int position = mViewPager.getCurrentItem();
                final Handler handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        if (position == 0){
                            /*mdataFromActivityToFragment.sendData(newText);*/
                            String tag = "android:switcher:" + R.id.container + ":" + position;
                            MedicineFragment mf = (MedicineFragment) getSupportFragmentManager().findFragmentByTag(tag);
                            mf.sendText(newText);

                        }else {
                            String tag = "android:switcher:" + R.id.container + ":" + position;
                            GeneralStoreFragment mf = (GeneralStoreFragment) getSupportFragmentManager().findFragmentByTag(tag);
                            mf.sendText(newText);
                        }
                    }
                };

                handler.postDelayed(r, 600);
                return true;
            }
        });

        medSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                final int position = mViewPager.getCurrentItem();
                final Handler handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        if (position == 0){
                            String tag = "android:switcher:" + R.id.container + ":" + position;
                            MedicineFragment mf = (MedicineFragment) getSupportFragmentManager().findFragmentByTag(tag);
                            mf.startControl();
                        }else {
                            String tag = "android:switcher:" + R.id.container + ":" + position;
                            GeneralStoreFragment gf = (GeneralStoreFragment) getSupportFragmentManager().findFragmentByTag(tag);
                            gf.startControl();
                        }
                    }
                };

                handler.postDelayed(r, 600);



            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_add_items_in_store_med, menu);

        // Get the notifications MenuItem and LayerDrawable (layer-list)
        MenuItem search = menu.findItem(R.id.action_search);
        medSearchView.setMenuItem(search);



        return true;
    }





    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MedicineFragment extends Fragment{

        public MedicineFragment() {
        }

        private RecyclerView medicineShow;
        private int max_id = 0;
        private int initial_max_id = 0;
        private String session_mob;
        private StoreManagementMedicineAdapter adapter;
        private List<StoreMedicineItem> medicineItemList;
        private CardView loadMore;
        private ProgressBar loadingLoadMore;
        private Animation downToUp, upToDown;
        private int prev_list_size = 0;
        private Timer timer;
        private Context context;
        SharedPreferences sp;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_medicine_in_store, container, false);
            loadUI(rootView);
            return rootView;
        }

        private void loadUI(View rootView) {

            sp = getActivity().getSharedPreferences("store_max_id", Activity.MODE_PRIVATE);
            max_id = sp.getInt("max_id_key", 0);
            initial_max_id = max_id;

            medicineShow = rootView.findViewById(R.id.medicineShow);
            loadMore = rootView.findViewById(R.id.load_more);
            loadingLoadMore = rootView.findViewById(R.id.loading_load_more);



            medicineItemList = new ArrayList<>();

            downToUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
            upToDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
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

            /*((AddItemsInStoreActivity) getActivity()).passVal(new DataFromActivityToFragment() {
                @Override
                public void sendData(String data) {

                }
            });*/


        }


        private void getSearchedMedView(final String med_name) {
            UserSessionManager userSessionManager = new UserSessionManager(context);
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


                                    StoreSearchedMedicineAdapter adapter = new StoreSearchedMedicineAdapter(medicineItemList, context);
                                    medicineShow.setAdapter(adapter);
                                    medicineShow.setLayoutManager(new LinearLayoutManager(context));





                                }else if (success.equals("2")){
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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

            Volley.newRequestQueue(context).add(stringRequest);



        }




        public void getMedView(){
            UserSessionManager userSessionManager = new UserSessionManager(context);
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

                                    adapter = new StoreManagementMedicineAdapter(medicineItemList, context);
                                    medicineShow.setAdapter(adapter);
                                    medicineShow.setLayoutManager(new LinearLayoutManager(context));
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
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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

            Volley.newRequestQueue(context).add(stringRequest);


        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            this.context = context;

        }

        public void sendText(final String newText){
            if (newText.length() > 0){
                //Toast.makeText(getApplicationContext(), s.toString(), Toast.LENGTH_SHORT).show();
                /*timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // do your actual work here
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                medicineItemList = new ArrayList<>();
                                getSearchedMedView(newText.toUpperCase());
                            }
                        });

                    }
                }, 600);*/
                medicineItemList = new ArrayList<>();
                getSearchedMedView(newText.toUpperCase());

            }else if (newText.matches("")){
                //Toast.makeText(getApplicationContext(), "Empty", Toast.LENGTH_SHORT).show();
                /*timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                max_id = initial_max_id;
                                medicineItemList = new ArrayList<>();
                                getMedView();
                            }
                        });

                    }
                }, 600);*/
                max_id = initial_max_id;
                medicineItemList = new ArrayList<>();
                getMedView();
            }
        }


        public void startControl(){
            max_id = initial_max_id;
            medicineItemList = new ArrayList<>();
            getMedView();
        }

    }

    public static class GeneralStoreFragment extends Fragment {

        public GeneralStoreFragment() {
        }

        private RecyclerView medicineShow;
        private int max_id = 0;
        private int initial_max_id = 0;
        private String session_mob;
        private GeneralStoreItemAdapter adapter;
        private List<GeneralStoreItem> generalStoreItems;
        private CardView loadMore;
        private ProgressBar loadingLoadMore;
        private Animation downToUp, upToDown;
        private int prev_list_size = 0;
        private Timer timer;
        SharedPreferences sp;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_general_item_in_store, container, false);
            loadUI(rootView);
            return rootView;
        }

        private void loadUI(View rootView) {

            sp = getActivity().getSharedPreferences("store_general_item_max_id", Activity.MODE_PRIVATE);
            max_id = sp.getInt("max_general_item_id_key", 0);
            initial_max_id = max_id;

            medicineShow = rootView.findViewById(R.id.generalItemShow);
            loadMore = rootView.findViewById(R.id.load_more);
            loadingLoadMore = rootView.findViewById(R.id.loading_load_more);


            generalStoreItems = new ArrayList<>();

            downToUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
            upToDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
            getMedView();


            loadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prev_list_size = generalStoreItems.size();
                    if (prev_list_size > 500){
                        generalStoreItems.clear();
                    }
                    getMedView();
                    loadMore.setVisibility(View.INVISIBLE);
                    loadingLoadMore.setVisibility(View.VISIBLE);
                }
            });


        }

        private void getSearchedMedView(final String med_name) {
            UserSessionManager userSessionManager = new UserSessionManager(getContext());
            HashMap<String, String> user = userSessionManager.getUserDetails();
            session_mob = user.get(UserSessionManager.KEY_MOB);
            String url =  "http://prescryp.com/prescriptionUpload/getSearchedGeneralItem.php";
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
                                JSONArray jsonArray = jsonObject.getJSONArray("general_item");

                                //Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                //int length = jsonArray.length();
                                //traversing through all the object
                                if (success.equals("1")){
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        //getting product object from json array
                                        JSONObject product = jsonArray.getJSONObject(i);

                                        //adding the product to product list
                                        boolean contain = false;
                                        for (GeneralStoreItem item : generalStoreItems){
                                            if (item.getName().equalsIgnoreCase(product.getString("Name"))){
                                                contain = true;
                                            }
                                        }
                                        if (!contain){
                                            generalStoreItems.add(new GeneralStoreItem(product.getString("Id"), product.getString("Name"),
                                                    product.getString("Title"), product.getString("Category"),
                                                    product.getString("Upper_Category"), "http://prescryp.com/prescriptionUpload/Item_Images/" + product.getString("Id") + ".jpg",
                                                    0, 0, "Add"));

                                        }



                                    }


                                    GeneralStoreSearchedAdapter adapter = new GeneralStoreSearchedAdapter(generalStoreItems, getContext());
                                    medicineShow.setAdapter(adapter);
                                    medicineShow.setLayoutManager(new LinearLayoutManager(getContext()));





                                }else if (success.equals("2")){
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
                    params.put("item_name", med_name);
                    return params;
                }
            };

            Volley.newRequestQueue(getContext()).add(stringRequest);



        }



        public void getMedView(){
            UserSessionManager userSessionManager = new UserSessionManager(getContext());
            HashMap<String, String> user = userSessionManager.getUserDetails();
            session_mob = user.get(UserSessionManager.KEY_MOB);
            String url =  "http://prescryp.com/prescriptionUpload/getGeneralItemToStore.php";
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
                                JSONArray jsonArray = jsonObject.getJSONArray("general_item");

                                //Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                //int length = jsonArray.length();
                                //traversing through all the object
                                if (success.equals("1")){
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        //getting product object from json array
                                        JSONObject product = jsonArray.getJSONObject(i);

                                        //adding the product to product list
                                        boolean contain = false;
                                        for (GeneralStoreItem item : generalStoreItems){
                                            if (item.getName().equalsIgnoreCase(product.getString("Name"))){
                                                contain = true;
                                            }
                                        }
                                        if (!contain){
                                            generalStoreItems.add(new GeneralStoreItem(product.getString("Id"), product.getString("Name"),
                                                    product.getString("Title"), product.getString("Category"),
                                                    product.getString("Upper_Category"), "http://prescryp.com/prescriptionUpload/Item_Images/" + product.getString("Id") + ".jpg",
                                                    0, 0, "Add"));

                                        }

                                        if (Integer.valueOf(product.getString("Id")) > max_id){
                                            max_id = Integer.valueOf(product.getString("Id"));
                                        }

                                    }

                                    adapter = new GeneralStoreItemAdapter(generalStoreItems, getContext());
                                    medicineShow.setAdapter(adapter);
                                    medicineShow.setLayoutManager(new LinearLayoutManager(getContext()));
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
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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

            Volley.newRequestQueue(getContext()).add(stringRequest);


        }

        public void sendText(final String newText){
            if (newText.length() > 0){
                //Toast.makeText(getApplicationContext(), s.toString(), Toast.LENGTH_SHORT).show();
                /*timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // do your actual work here
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                generalStoreItems = new ArrayList<>();
                                getSearchedMedView(newText.toUpperCase());
                            }
                        });

                    }
                }, 600);*/
                generalStoreItems = new ArrayList<>();
                getSearchedMedView(newText.toUpperCase());

            }else if (newText.matches("")){
                //Toast.makeText(getApplicationContext(), "Empty", Toast.LENGTH_SHORT).show();
                /*timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                max_id = initial_max_id;
                                generalStoreItems = new ArrayList<>();
                                getMedView();
                            }
                        });

                    }
                }, 600);*/
                max_id = initial_max_id;
                generalStoreItems = new ArrayList<>();
                getMedView();

            }
        }

        public void startControl(){
            max_id = initial_max_id;
            generalStoreItems = new ArrayList<>();
            getMedView();
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
                    return new AddItemsInStoreActivity.MedicineFragment();
                case 1 :
                    return new AddItemsInStoreActivity.GeneralStoreFragment();

                default: return null;
            }
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }
}
