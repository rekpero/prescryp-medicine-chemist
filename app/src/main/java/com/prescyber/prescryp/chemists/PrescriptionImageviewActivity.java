package com.prescyber.prescryp.chemists;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PrescriptionImageviewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView presImageView;
    private ConstraintLayout verification_layout, verify_layout, reject_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prescription_imageview_bar_main);

        toolbar = findViewById(R.id.prescription_image_view_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        presImageView = findViewById(R.id.prescriptionView);
        verification_layout = findViewById(R.id.verification_layout);
        verify_layout = findViewById(R.id.verify_layout);
        reject_layout = findViewById(R.id.reject_layout);

        if(getIntent().getStringExtra("PrescriptionId") != null && getIntent().getStringExtra("Date") != null
                && getIntent().getStringExtra("ImagePathUrl") != null && getIntent().getStringExtra("Sender") != null
                && getIntent().getStringExtra("Status") != null && getIntent().getStringExtra("NumberOfPrescription") != null){
            final String prescriptionId = getIntent().getStringExtra("PrescriptionId");
            final String date = getIntent().getStringExtra("Date");
            final String imgUrl = getIntent().getStringExtra("ImagePathUrl");
            final String sender = getIntent().getStringExtra("Sender");
            final String status = getIntent().getStringExtra("Status");
            final String order_number = getIntent().getStringExtra("Order_Number");
            final String numberOfPrescription = getIntent().getStringExtra("NumberOfPrescription");
            final String patient_mobile_number = getIntent().getStringExtra("Patient_Mobile_number");
            getSupportActionBar().setTitle(prescriptionId);
            String date_shown  = getDateFormatChangeSubtitle(date);
            toolbar.setSubtitle(date_shown);
            Picasso.get()
                    .load(imgUrl)
                    .fit()
                    .into(presImageView);

            if (sender.equalsIgnoreCase("Verification")){
                verification_layout.setVisibility(View.VISIBLE);

            }else if (sender.equalsIgnoreCase("See Prescription")){
                verification_layout.setVisibility(View.GONE);
            }

            verify_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verifyPrescription(prescriptionId, "Verified", order_number);
                }
            });

            reject_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //verifyPrescription(prescriptionId, "Rejected");
                    Intent intent = new Intent(PrescriptionImageviewActivity.this, RejectReasonActivity.class);
                    intent.putExtra("Order_Number", order_number);
                    intent.putExtra("Patient_Mobile_number", patient_mobile_number);
                    intent.putExtra("NumberOfPrescription", numberOfPrescription);
                    intent.putExtra("PrescriptionId", prescriptionId);
                    startActivity(intent);
                }
            });

        }


    }

    private void verifyPrescription(final String prescription_id, final String status, final String order_number) {
        String url = "http://prescryp.com/prescriptionUpload/verifyPrescription.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")){
                        Toast.makeText(getApplicationContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
                        verification_layout.setVisibility(View.GONE);
                        Intent intent = new Intent(PrescriptionImageviewActivity.this, OrderDetailsActivity.class);
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
                params.put("prescriptionId", prescription_id);
                params.put("status", status);
                params.put("order_number", order_number);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


    private String getDateFormatChangeSubtitle(String date){
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        String outputDateStr = "";
        try {
            Date new_date = inputFormat.parse(date);
            outputDateStr = outputFormat.format(new_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDateStr;
    }
}
