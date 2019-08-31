package com.prescyber.prescryp.chemists;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RejectReasonActivity extends AppCompatActivity {

    private ConstraintLayout invalid_prescription_layout, ordered_medicine_layout, blurry_prescription_layout, other_issue_layout;
    private String number_of_prescription, order_number, prescription_id, patient_mobile_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_reason);
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

        invalid_prescription_layout = findViewById(R.id.invalid_prescription_layout);
        ordered_medicine_layout = findViewById(R.id.ordered_medicine_layout);
        blurry_prescription_layout = findViewById(R.id.blurry_prescription_layout);
        other_issue_layout = findViewById(R.id.other_issue_layout);

        if (getIntent() != null){
            order_number = getIntent().getStringExtra("Order_Number");
            number_of_prescription = getIntent().getStringExtra("NumberOfPrescription");
            prescription_id = getIntent().getStringExtra("PrescriptionId");
            patient_mobile_number = getIntent().getStringExtra("Patient_Mobile_number");
        }

        invalid_prescription_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.valueOf(number_of_prescription) > 1){
                    Intent intent = new Intent(RejectReasonActivity.this, RejectMedicinesActivity.class);
                    intent.putExtra("Order_Number", order_number);
                    intent.putExtra("PrescriptionId", prescription_id);
                    intent.putExtra("Reject_Reason", "Invalid prescription format");
                    startActivity(intent);
                }else {
                    verifyPrescription(prescription_id, order_number, "Declined", "Invalid prescription format");
                }
            }
        });

        ordered_medicine_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RejectReasonActivity.this, RejectMedicinesActivity.class);
                intent.putExtra("Order_Number", order_number);
                intent.putExtra("PrescriptionId", prescription_id);
                intent.putExtra("Reject_Reason", "Ordered medicine not in prescription");
                startActivity(intent);
            }
        });

        blurry_prescription_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.valueOf(number_of_prescription) > 1){
                    Intent intent = new Intent(RejectReasonActivity.this, RejectMedicinesActivity.class);
                    intent.putExtra("Order_Number", order_number);
                    intent.putExtra("PrescriptionId", prescription_id);
                    intent.putExtra("Reject_Reason", "Blurry prescription image");
                    startActivity(intent);
                }else {
                    verifyPrescription(prescription_id, order_number, "Declined", "Blurry prescription image");
                }
            }
        });

        other_issue_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void verifyPrescription(final String prescription_id, final String order_number, final String status, final String reject_reason) {
        String url = "http://prescryp.com/prescriptionUpload/rejectCompleteOrder.php";
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
                        confirmedOrderToPatientFCM(patient_mobile_number, order_number, status, reject_reason);
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
                params.put("prescriptionId", prescription_id);
                params.put("status", status);
                params.put("order_number", order_number);
                params.put("reject_reason", reject_reason);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void confirmedOrderToPatientFCM(final String mobile_number, final String order_number, final String status, final String reject_reason) {
        String url = "http://prescryp.com/prescriptionUpload/sendConfirmationNotification.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if (success.equalsIgnoreCase("1")){
                        Toast.makeText(getApplicationContext(), status + " Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RejectReasonActivity.this, OrderDetailsActivity.class);
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
                params.put("patient_mobile_number", mobile_number);
                params.put("order_status", status);
                params.put("reject_reason", reject_reason);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


}
