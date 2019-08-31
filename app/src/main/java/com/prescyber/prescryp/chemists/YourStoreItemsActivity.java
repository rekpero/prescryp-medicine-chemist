package com.prescyber.prescryp.chemists;

import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
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
import android.widget.TextView;
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

public class YourStoreItemsActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private MaterialSearchView medSearchView;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_store_items);

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
        mSectionsPagerAdapter = new YourStoreItemsActivity.SectionsPagerAdapter(getSupportFragmentManager(), 2);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        medSearchView = findViewById(R.id.searchStoreMedicine);



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
                            GeneralStoreFragment gf = (GeneralStoreFragment) getSupportFragmentManager().findFragmentByTag(tag);
                            gf.sendText(newText);
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
        getMenuInflater().inflate(R.menu.menu_your_store_items, menu);

        // Get the notifications MenuItem and LayerDrawable (layer-list)
        MenuItem search = menu.findItem(R.id.action_search);
        medSearchView.setMenuItem(search);



        return true;
    }



    public static class MedicineFragment extends Fragment {


        public MedicineFragment() {
        }

        private RecyclerView storeMedicineShow;
        private String session_mob;
        private StoreManagementMedicineAdapter adapter;
        private List<StoreMedicineItem> medicineItemList;
        private CardView loadMore;
        private ProgressBar loadingLoadMore;
        private int max_id = 0;
        private Animation downToUp, upToDown;
        private int prev_list_size = 0;
        private TextView notAddedMedicine;
        private Timer timer;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_medicine_in_store, container, false);
            loadUI(rootView);
            return rootView;
        }

        private void loadUI(View rootView) {
            storeMedicineShow = rootView.findViewById(R.id.store_medicine_list);
            loadMore = rootView.findViewById(R.id.load_more);
            loadingLoadMore = rootView.findViewById(R.id.loading_load_more);
            notAddedMedicine = rootView.findViewById(R.id.not_added_medicines_text_view);
            notAddedMedicine.setVisibility(View.INVISIBLE);


            medicineItemList = new ArrayList<>();

            downToUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
            upToDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
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



        }


        private void getSearchedMedView(final String med_name) {
            UserSessionManager userSessionManager = new UserSessionManager(getContext());
            HashMap<String, String> user = userSessionManager.getUserDetails();
            session_mob = user.get(UserSessionManager.KEY_MOB);
            String url =  "http://prescryp.com/prescriptionUpload/getSearchedAddedStore.php";
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
                                                            product.getString("medicine_form"), packaging, Integer.valueOf(product.getString("quantity")),
                                                            product.getString("check_pres"), "UPDATE"));
                                                }

                                            }
                                        }


                                    }


                                    StoreSearchedMedicineAdapter adapter = new StoreSearchedMedicineAdapter(medicineItemList, getContext());
                                    storeMedicineShow.setAdapter(adapter);
                                    storeMedicineShow.setLayoutManager(new LinearLayoutManager(getContext()));






                                }else if (success.equals("2")){
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    loadingLoadMore.setVisibility(View.INVISIBLE);
                                    storeMedicineShow.setVisibility(View.GONE);
                                    notAddedMedicine.setVisibility(View.VISIBLE);

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

            Volley.newRequestQueue(getContext()).add(stringRequest);

        }


        public void getMedView(){
            UserSessionManager userSessionManager = new UserSessionManager(getContext());
            HashMap<String, String> user = userSessionManager.getUserDetails();
            session_mob = user.get(UserSessionManager.KEY_MOB);
            String url =  "http://prescryp.com/prescriptionUpload/getStoresAddedMedicines.php";
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
                                                        product.getString("medicine_form"), packaging, Integer.valueOf(product.getString("quantity")),
                                                        product.getString("check_pres"), "UPDATE"));
                                            }
                                        }

                                        if (Integer.valueOf(product.getString("id")) > max_id){
                                            max_id = Integer.valueOf(product.getString("id"));
                                        }

                                    }


                                    adapter = new StoreManagementMedicineAdapter(medicineItemList, getContext());
                                    storeMedicineShow.setAdapter(adapter);
                                    storeMedicineShow.setLayoutManager(new LinearLayoutManager(getContext()));
                                    storeMedicineShow.scrollToPosition(prev_list_size - 1);
                                    adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                                        @Override
                                        public void onBottomReached(int position) {
                                            //your code goes here
                                            if (medicineItemList.size() > 20){
                                                loadMore.setVisibility(View.VISIBLE);
                                                loadMore.setAnimation(downToUp);
                                            }
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
                                    loadingLoadMore.setVisibility(View.INVISIBLE);
                                    storeMedicineShow.setVisibility(View.GONE);
                                    notAddedMedicine.setVisibility(View.VISIBLE);

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
                medicineItemList.clear();
                storeMedicineShow.getRecycledViewPool().clear();
                getSearchedMedView(newText.toUpperCase());
            }else if (newText.matches("")){
                medicineItemList.clear();
                max_id = 0;
                storeMedicineShow.getRecycledViewPool().clear();
                getMedView();
            }
        }


        public void startControl(){
            medicineItemList.clear();
            max_id = 0;
            storeMedicineShow.getRecycledViewPool().clear();
            getMedView();
        }


    }

    public static class GeneralStoreFragment extends Fragment {

        public GeneralStoreFragment() {
        }

        private RecyclerView storeMedicineShow;
        private String session_mob;
        private GeneralStoreItemAdapter adapter;
        private List<GeneralStoreItem> generalStoreItems;
        private CardView loadMore;
        private ProgressBar loadingLoadMore;
        private int max_id = 0;
        private Animation downToUp, upToDown;
        private int prev_list_size = 0;
        private ConstraintLayout showStoreMedicines;
        private TextView notAddedMedicine;
        private Timer timer;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_general_item_in_store, container, false);
            loadUI(rootView);
            return rootView;
        }

        private void loadUI(View rootView) {
            storeMedicineShow = rootView.findViewById(R.id.store_medicine_list);
            loadMore = rootView.findViewById(R.id.load_more);
            loadingLoadMore = rootView.findViewById(R.id.loading_load_more);
            notAddedMedicine = rootView.findViewById(R.id.not_added_medicines_text_view);
            notAddedMedicine.setVisibility(View.INVISIBLE);



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
            String url =  "http://prescryp.com/prescriptionUpload/getSearchedAddedGeneralItem.php";
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
                                                    Integer.valueOf(product.getString("quantity")), 0, "Update"));

                                        }



                                    }


                                    GeneralStoreSearchedAdapter adapter = new GeneralStoreSearchedAdapter(generalStoreItems, getContext());
                                    storeMedicineShow.setAdapter(adapter);
                                    storeMedicineShow.setLayoutManager(new LinearLayoutManager(getContext()));





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
            String url =  "http://prescryp.com/prescriptionUpload/getAddedGeneralItem.php";
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
                                            generalStoreItems.add(new GeneralStoreItem(product.getString("id"), product.getString("Name"),
                                                    product.getString("Title"), product.getString("Category"),
                                                    product.getString("Upper_Category"), "http://prescryp.com/prescriptionUpload/Item_Images/" + product.getString("id") + ".jpg",
                                                    Integer.valueOf(product.getString("quantity")), 0, "Update"));

                                        }

                                        if (Integer.valueOf(product.getString("id")) > max_id){
                                            max_id = Integer.valueOf(product.getString("id"));
                                        }

                                    }

                                    adapter = new GeneralStoreItemAdapter(generalStoreItems, getContext());
                                    storeMedicineShow.setAdapter(adapter);
                                    storeMedicineShow.setLayoutManager(new LinearLayoutManager(getContext()));
                                    storeMedicineShow.scrollToPosition(prev_list_size - 1);
                                    adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                                        @Override
                                        public void onBottomReached(int position) {
                                            //your code goes here
                                            if (generalStoreItems.size() > 20){
                                                loadMore.setVisibility(View.VISIBLE);
                                                loadMore.setAnimation(downToUp);
                                            }

                                        }
                                    });

                                    adapter.setOnNotReachedBottomListener(new OnNotReachedBottomListener(){

                                        @Override
                                        public void onNotReachedBottom(int position) {
                                            loadMore.setVisibility(View.GONE);
                                            loadMore.setAnimation(upToDown);
                                        }
                                    });

                                    if (generalStoreItems.size() < 20){
                                        loadMore.setVisibility(View.GONE);
                                        loadMore.setAnimation(upToDown);
                                    }
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
                generalStoreItems.clear();
                storeMedicineShow.getRecycledViewPool().clear();
                getSearchedMedView(newText.toUpperCase());

            }else if (newText.matches("")){
                generalStoreItems.clear();
                max_id = 0;
                storeMedicineShow.getRecycledViewPool().clear();
                getMedView();
            }
        }


        public void startControl(){
            generalStoreItems.clear();
            max_id = 0;
            storeMedicineShow.getRecycledViewPool().clear();
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
                    return new YourStoreItemsActivity.MedicineFragment();
                case 1 :
                    return new YourStoreItemsActivity.GeneralStoreFragment();

                default: return null;
            }
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }
}
