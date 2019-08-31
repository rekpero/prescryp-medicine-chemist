package com.prescyber.prescryp.chemists;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prescyber.prescryp.chemists.Adapter.OrderListAdapter;
import com.prescyber.prescryp.chemists.Adapter.RejectMedicineAdapter;
import com.prescyber.prescryp.chemists.Model.CartItem;
import com.prescyber.prescryp.chemists.Model.RejectMedicineItem;
import com.prescyber.prescryp.chemists.SessionManager.UserSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class RejectMedicinesActivity extends AppCompatActivity {

    private RecyclerView reject_medicine_list;
    private ConstraintLayout reject_medicine;
    private String order_number, prescription_id, reject_reason, complete_reason;
    private List<RejectMedicineItem> rejectMedicineItems;
    private CountDownLatch latch;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_medicines);
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

        reject_medicine_list = findViewById(R.id.reject_medicine_list);
        reject_medicine = findViewById(R.id.reject_medicine);

        if (getIntent() != null){
            order_number = getIntent().getStringExtra("Order_Number");
            reject_reason = getIntent().getStringExtra("Reject_Reason");
            prescription_id = getIntent().getStringExtra("PrescriptionId");
        }

        rejectMedicineItems = new ArrayList<>();
        getMedicines(order_number);



        reject_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complete_reason = "";
                mDialog = new ProgressDialog(RejectMedicinesActivity.this);
                mDialog.setMessage("Declining Order...");
                mDialog.show();
                for (RejectMedicineItem item : rejectMedicineItems){
                    if (item.isSelected()){
                        complete_reason = complete_reason + item.getMedicineName() + ", ";
                        updateRejectItem(item.getMedicineName(), item.getMedicineContains());
                    }
                }

                complete_reason = complete_reason.substring(0, complete_reason.length()-2);
                complete_reason = complete_reason + " couldn't be delivered due to " + reject_reason;

                updateRejectPartially(prescription_id, order_number, reject_reason, "Rejected");


            }
        });

    }

    private void updateRejectPartially(final String prescription_id, final String order_number, final String reject_reason, final String status) {
        String url = "http://prescryp.com/prescriptionUpload/rejectOrderPartially.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")){

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        Intent intent = new Intent(RejectMedicinesActivity.this, OrderDetailsActivity.class);
                        intent.putExtra("Order_Number", order_number);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("order_number", order_number);
                params.put("prescriptionId", prescription_id);
                params.put("reject_reason", reject_reason);
                params.put("status", status);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

    }

    private void updateRejectItem(final String medicineName, final String medicineContains) {
        String url = "http://prescryp.com/prescriptionUpload/rejectMedicines.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")){

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("order_number", order_number);
                params.put("medicine_name", medicineName);
                params.put("medicine_contain", medicineContains);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

    }

    private void getMedicines(final String order_number) {
        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = userSessionManager.getUserDetails();
        final String session_mob = user.get(UserSessionManager.KEY_MOB);
        String url = "http://prescryp.com/prescriptionUpload/getOrderItemChemist.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    JSONArray jsonArray = jsonObject.getJSONArray("order_items");
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")){
                        for (int i = 0; i < jsonArray.length(); i++) {

                            //getting product object from json array
                            JSONObject order_item = jsonArray.getJSONObject(i);

                            String medicine_name = order_item.getString("medicine_name");
                            String package_contain = order_item.getString("package_contain");

                            rejectMedicineItems.add(new RejectMedicineItem(medicine_name, package_contain, false));

                        }

                        RejectMedicineAdapter adapter = new RejectMedicineAdapter(rejectMedicineItems, getApplicationContext());
                        reject_medicine_list.setAdapter(adapter);
                        reject_medicine_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        reject_medicine_list.setHasFixedSize(true);



                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("order_number", order_number);
                params.put("mobile_number", session_mob);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

    }

}
