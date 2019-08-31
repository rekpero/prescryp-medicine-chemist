package com.prescyber.prescryp.chemists;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prescyber.prescryp.chemists.Adapter.PrescriptionMedicineAdapter;
import com.prescyber.prescryp.chemists.Adapter.PrescriptionTestRemarkAdapter;
import com.prescyber.prescryp.chemists.Model.MedicineListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DigitalPrescriptionActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String prescriptionId, sender_key, date;
    private ProgressBar progressBar;
    private PrescriptionMedicineAdapter medicineAdapter;
    private PrescriptionTestRemarkAdapter testAdapter, remarkAdapter;
    private LinearLayout medLinearLayout, testLinearLayout, remarkLinearLayout;
    private RecyclerView medRecyclerView, testRecyclerView, remarkRecyclerView;
    private TextView institutionName, intitutionAddr, doctorName, doctorQualification, doctorContact, doctorRegNum,
            dateOfConsultation, patientName, patientAge, patientSex;
    private View divider;
    private Animation leftToRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.digital_prescription_bar_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(getIntent().getStringExtra("PrescriptionId") != null && getIntent().getStringExtra("Date") != null){
            prescriptionId = getIntent().getStringExtra("PrescriptionId");
            getSupportActionBar().setTitle(prescriptionId);
            date = getIntent().getStringExtra("Date");
            String date_shown = getDateFormatChangeSubtitle(date);
            toolbar.setSubtitle(date_shown);
            sender_key = getIntent().getStringExtra("Sender");

        }

        init();

        getPrescriptionDetails(prescriptionId);

        ArrayList<MedicineListItem> medicineListItems = new ArrayList<>();
        ArrayList<String> testListItems = new ArrayList<>();
        ArrayList<String> remarkListItems = new ArrayList<>();

        getMedicines(prescriptionId, medicineListItems);

        getTestsView(prescriptionId, testListItems);

        getRemarkView(prescriptionId, remarkListItems);



    }

    private void init(){
        medRecyclerView = findViewById(R.id.medRecyclerView);
        testRecyclerView = findViewById(R.id.testRecyclerView);
        remarkRecyclerView = findViewById(R.id.remarkRecyclerView);

        medLinearLayout = findViewById(R.id.medicineLayout);
        testLinearLayout = findViewById(R.id.testLayout);
        remarkLinearLayout = findViewById(R.id.remarkLayout);

        institutionName = findViewById(R.id.institution_name);
        intitutionAddr = findViewById(R.id.institution_addr);
        dateOfConsultation = findViewById(R.id.date_of_consultation);

        doctorName = findViewById(R.id.doctor_name);
        doctorQualification = findViewById(R.id.doctor_qualification);
        doctorContact = findViewById(R.id.doctor_contact);
        doctorRegNum = findViewById(R.id.doctor_reg_num);

        divider = findViewById(R.id.horDivider);

        patientName = findViewById(R.id.patient_name);
        patientAge = findViewById(R.id.patient_age);
        patientSex = findViewById(R.id.patient_sex);

        progressBar = findViewById(R.id.loading_bar);

        leftToRight = AnimationUtils.loadAnimation(this, R.anim.custom_for_digital_pres);
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

    private void getPrescriptionDetails(final String prescriptionId){
        String url =  "http://prescryp.com/prescriptionUpload/getPrescriptionInfo.php";
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

                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            //traversing through all the object
                            if (success.equals("1")){
                                String doctor_name_qualification = jsonObject.getString("doctor_name_qualification");
                                String doctor_contact_details = "Ph: " +jsonObject.getString("doctor_contact_details");
                                String institution_name = jsonObject.getString("institution_name");
                                String institution_address = jsonObject.getString("institution_address");
                                String registration_number = jsonObject.getString("registration_number");
                                String patient_name = jsonObject.getString("patient_name");
                                String patient_age = jsonObject.getString("patient_age");
                                String patient_sex = jsonObject.getString("patient_sex");
                                String patient_date_of_consultation = jsonObject.getString("patient_date_of_consultation");

                                String date_of_consultation = "Date: " + getDateFormatChange(patient_date_of_consultation);

                                String patient_name_full = "Name: " + patient_name;
                                String patient_age_full = "Age: " + patient_age + " yrs";
                                String patient_sex_full = "Sex: " + patient_sex;

                                String reg_num = "Reg No. : " + registration_number;

                                String[] doctor_name_detail = doctor_name_qualification.split(",");
                                String doctor_name = doctor_name_detail[0].trim();
                                String doctor_qualification = doctor_name_detail[1].trim();


                                institutionName.setText(institution_name);
                                intitutionAddr.setText(institution_address);
                                dateOfConsultation.setText(date_of_consultation);

                                doctorName.setText(doctor_name);
                                doctorQualification.setText(doctor_qualification);
                                doctorContact.setText(doctor_contact_details);
                                doctorRegNum.setText(reg_num);

                                divider.setVisibility(View.VISIBLE);
                                divider.setAnimation(leftToRight);

                                patientName.setText(patient_name_full);
                                patientAge.setText(patient_age_full);
                                patientSex.setText(patient_sex_full);


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
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("prescription_id", prescriptionId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);


    }

    private String getDateFormatChange(String date){
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yy", Locale.US);
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

    private void getMedicines(final String prescriptionId, final ArrayList<MedicineListItem> medicineListItems){
        String url =  "http://prescryp.com/prescriptionUpload/getMedicineListInfo.php";
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

                            //traversing through all the object
                            if (success.equals("1")){
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //getting product object from json array
                                    JSONObject product = jsonArray.getJSONObject(i);

                                    //adding the product to product list
                                    MedicineListItem medicineListItem = new MedicineListItem(
                                            product.getString("medicine_name"),
                                            product.getString("medicine_strength"),
                                            product.getString("medicine_dose"),
                                            product.getString("medicine_duration"));
                                    medicineListItems.add(medicineListItem);

                                }

                                medLinearLayout.setVisibility(View.VISIBLE);
                                medLinearLayout.setAnimation(leftToRight);
                                medicineAdapter = new PrescriptionMedicineAdapter(medicineListItems, DigitalPrescriptionActivity.this);
                                medRecyclerView.setAdapter(medicineAdapter);
                                medRecyclerView.setLayoutManager(new LinearLayoutManager(DigitalPrescriptionActivity.this));




                            }else if (success.equals("2")){
                                medLinearLayout.setVisibility(View.GONE);
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
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("prescription_id", prescriptionId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);


    }

    public void getTestsView(final String prescriptionId, final ArrayList<String> testListItems){
        String url =  "http://prescryp.com/prescriptionUpload/getTestsListInfo.php";
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
                            JSONArray jsonArray = jsonObject.getJSONArray("tests");
                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            //traversing through all the object
                            if (success.equals("1")){
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //getting product object from json array
                                    JSONObject product = jsonArray.getJSONObject(i);

                                    //adding the product to product list
                                    testListItems.add(product.get("test_name").toString());

                                }

                                testLinearLayout.setVisibility(View.VISIBLE);
                                testLinearLayout.setAnimation(leftToRight);
                                testAdapter = new PrescriptionTestRemarkAdapter(testListItems, DigitalPrescriptionActivity.this);
                                testRecyclerView.setAdapter(testAdapter);
                                testRecyclerView.setLayoutManager(new LinearLayoutManager(DigitalPrescriptionActivity.this));




                            }else if (success.equals("2")){
                                testLinearLayout.setVisibility(View.GONE);
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
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("prescription_id", prescriptionId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);


    }

    public void getRemarkView(final String prescriptionId, final ArrayList<String> remarkListItems){
        String url =  "http://prescryp.com/prescriptionUpload/getRemarkListInfo.php";
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
                            JSONArray jsonArray = jsonObject.getJSONArray("remarks");
                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            //traversing through all the object
                            if (success.equals("1")){

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //getting product object from json array
                                    JSONObject product = jsonArray.getJSONObject(i);

                                    //adding the product to product list
                                    remarkListItems.add(product.get("comment").toString());

                                }
                                remarkLinearLayout.setVisibility(View.VISIBLE);
                                remarkLinearLayout.setAnimation(leftToRight);
                                remarkAdapter = new PrescriptionTestRemarkAdapter(remarkListItems, DigitalPrescriptionActivity.this);
                                remarkRecyclerView.setAdapter(remarkAdapter);
                                remarkRecyclerView.setLayoutManager(new LinearLayoutManager(DigitalPrescriptionActivity.this));

                                progressBar.setVisibility(View.GONE);




                            }else if (success.equals("2")){
                                remarkLinearLayout.setVisibility(View.GONE);
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
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("prescription_id", prescriptionId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);


    }
}
