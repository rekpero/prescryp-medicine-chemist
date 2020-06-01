package com.prescyber.prescryp.chemists;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.prescyber.prescryp.chemists.Adapter.OrderListAdapter;
import com.prescyber.prescryp.chemists.Adapter.PrescriptionForOrderAdapter;
import com.prescyber.prescryp.chemists.Model.CartItem;
import com.prescyber.prescryp.chemists.Model.ListItem;
import com.prescyber.prescryp.chemists.SessionManager.UserSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class OrderDetailsActivity extends AppCompatActivity {
    private String order_number, patient_mobile_number, order_status, delivery_person_verification_status;
    private TextView date_of_order_text_view, grand_total_text_view, patient_name_text, complete_delivery_address_text;
    private TextView paid_via_text, phone_number_text, status_text;
    private TextView attached_prescription_number, order_size, commission_text_view, your_earning_text_view;
    private TextView expected_delivery_text, expected_delivery_heading_text, earning_heading_text, earning_text;
    private TextView delivery_person_name_text, delivery_phone_number_text;
    private RecyclerView order_list_recycler, attached_prescription_list_recycler;
    private ProgressBar loading_order_tracking;
    private ScrollView track_order_view;
    private List<CartItem> order_list;
    private List<ListItem> attached_prescription_list;
    private CardView attachedPrescriptionCard;
    private ConstraintLayout dispatch_layout, confirmation_layout, confirm_layout, decline_layout;
    private CountDownLatch latch;
    private ProgressDialog mDialog;
    private Boolean pres_verification_done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent() != null){
            order_number = getIntent().getStringExtra("Order_Number");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
        String title = "Order #" + order_number;
        toolbar.setTitle(title);
        init();

        latch = new CountDownLatch(3);

        order_list = new ArrayList<>();
        attached_prescription_list = new ArrayList<>();

        getOrderForTracking(order_number);

        getOrderItem(order_number);
        getAttachPrescription(order_number);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                    OrderDetailsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading_order_tracking.setVisibility(View.GONE);
                            track_order_view.setVisibility(View.VISIBLE);
                            if (order_status.equalsIgnoreCase("Placed") && pres_verification_done){
                                confirmation_layout.setVisibility(View.VISIBLE);
                            }else if (order_status.equalsIgnoreCase("Confirmed")){
                                dispatch_layout.setVisibility(View.VISIBLE);
                            }else {
                                confirmation_layout.setVisibility(View.GONE);
                                dispatch_layout.setVisibility(View.GONE);
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        dispatch_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showVerificationPopup();

            }
        });


        confirm_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = new ProgressDialog(OrderDetailsActivity.this);
                mDialog.setMessage("Confirming Order...");
                orderStatusUpdate(order_number, "Confirmed");
            }
        });

        decline_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = new ProgressDialog(OrderDetailsActivity.this);
                mDialog.setMessage("Declining Order...");
                orderStatusUpdate(order_number, "Declined");
            }
        });

    }

    private void showVerificationPopup() {
        final PinView otp_pin;
        TextView submit_code, cancel_code;
        final Dialog otpDialog = new Dialog(OrderDetailsActivity.this);
        otpDialog.setContentView(R.layout.verification_otp_popup);
        otp_pin = otpDialog.findViewById(R.id.otp_pin);
        submit_code = otpDialog.findViewById(R.id.submit_code);
        cancel_code = otpDialog.findViewById(R.id.cancel_code);

        cancel_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otpDialog.dismiss();
            }
        });

        submit_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmVerificationCode(order_number, otp_pin.getText().toString(), otpDialog);
            }
        });

        otpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        otpDialog.show();

    }

    private void confirmVerificationCode(final String order_number, final String otp_code, final Dialog otpDialog) {
        String url = "http://prescryp.com/prescriptionUpload/verifyDeliveryPerson.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")){
                        otpDialog.dismiss();
                        mDialog = new ProgressDialog(OrderDetailsActivity.this);
                        mDialog.setMessage("Dispatching Order...");
                        mDialog.show();
                        dispatchOrderToPatientUpdate(order_number);

                    }else if (success.equalsIgnoreCase("2")){
                        showVerificationPopup();
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
                params.put("verification_code", otp_code);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void init() {
        date_of_order_text_view = findViewById(R.id.date_of_order);
        grand_total_text_view = findViewById(R.id.grand_total_text_view);
        order_size = findViewById(R.id.order_size_text);
        order_list_recycler = findViewById(R.id.order_list_recycler);
        loading_order_tracking = findViewById(R.id.loading_order_tracking);
        track_order_view = findViewById(R.id.track_order_view);
        paid_via_text = findViewById(R.id.paid_via_text);
        phone_number_text = findViewById(R.id.phone_number_text);
        complete_delivery_address_text = findViewById(R.id.complete_delivery_address_text);
        attached_prescription_list_recycler = findViewById(R.id.attached_prescription_list);
        attached_prescription_number = findViewById(R.id.attached_prescription_number);
        attachedPrescriptionCard = findViewById(R.id.attachedPrescriptionCard);
        dispatch_layout = findViewById(R.id.dispatch_layout);
        patient_name_text = findViewById(R.id.patient_name_text);
        commission_text_view = findViewById(R.id.commission_text_view);
        your_earning_text_view = findViewById(R.id.your_earning_text_view);
        status_text = findViewById(R.id.status_text);
        expected_delivery_text = findViewById(R.id.expected_delivery_text);
        expected_delivery_heading_text = findViewById(R.id.expected_delivery_heading_text);
        earning_heading_text = findViewById(R.id.earning_heading_text);
        earning_text = findViewById(R.id.earning_text);
        delivery_person_name_text = findViewById(R.id.delivery_person_name_text);
        delivery_phone_number_text = findViewById(R.id.delivery_phone_number_text);
        confirmation_layout = findViewById(R.id.confirmation_layout);
        confirm_layout = findViewById(R.id.confirm_layout);
        decline_layout = findViewById(R.id.decline_layout);
    }


    private void orderStatusUpdate(final String order_number, final String status) {
        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = userSessionManager.getUserDetails();
        final String session_mob = user.get(UserSessionManager.KEY_MOB);
        String url = "http://prescryp.com/prescriptionUpload/updateStatus.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")){
                        confirmedOrderToPatientFCM(patient_mobile_number, order_number, status);
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
                params.put("order_status", status);
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void dispatchOrderToPatientUpdate(final String order_number) {
        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = userSessionManager.getUserDetails();
        final String session_mob = user.get(UserSessionManager.KEY_MOB);
        String url = "http://prescryp.com/prescriptionUpload/updateStatus.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")){
                        dispatchOrderToPatientFCM(patient_mobile_number, order_number);
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
                params.put("order_status", "Dispatched");
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void confirmedOrderToPatientFCM(final String mobile_number, final String order_number, final String status) {
        String url = "http://prescryp.com/prescriptionUpload/sendConfirmationNotification.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if (success.equalsIgnoreCase("1")){
                        Toast.makeText(getApplicationContext(), status + " Successfully", Toast.LENGTH_SHORT).show();
                        if (!status.equalsIgnoreCase("Declined")){
                            dispatch_layout.setVisibility(View.VISIBLE);
                            status_text.setText("Confirmed");
                        }else {
                            dispatch_layout.setVisibility(View.GONE);
                            status_text.setText("Declined");
                        }
                        confirmation_layout.setVisibility(View.GONE);

                        mDialog.dismiss();
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
                params.put("reject_reason", "null");
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


    private void dispatchOrderToPatientFCM(final String mobile_number, final String order_number) {
        String url = "http://prescryp.com/prescriptionUpload/sendDispatchedNotification.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");

                    if (success.equalsIgnoreCase("1")){
                        Toast.makeText(getApplicationContext(), "Dispatched Successfully", Toast.LENGTH_SHORT).show();
                        dispatch_layout.setVisibility(View.GONE);
                        mDialog.dismiss();
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
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private String getDateFormatChange(String date){
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

    private void getOrderForTracking(final String order_number) {
        String url = "http://prescryp.com/prescriptionUpload/getOrderForTracking.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    String date_of_order = jsonObject.getString("date_of_order");
                    String time_of_order = jsonObject.getString("time_of_order");
                    String payment_type = jsonObject.getString("payment_type");
                    patient_mobile_number = jsonObject.getString("patient_mobile_number");
                    String patient_name = jsonObject.getString("patient_name");
                    String delivery_complete_address = jsonObject.getString("delivery_complete_address");
                    String delivery_date = jsonObject.getString("delivery_date");
                    String delivery_time = jsonObject.getString("delivery_time");
                    order_status = jsonObject.getString("order_status");
                    String delivery_person_mobile_number = jsonObject.getString("delivery_person_mobile_number");
                    String delivery_person_name = jsonObject.getString("name");
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")){
                        String date_change_format = getDateFormatChange(date_of_order);
                        String[] time_list = time_of_order.split(" ");
                        String time_changed = time_list[0] + " " + time_list[1].toUpperCase();

                        String date_shown = date_change_format.trim() + ", " + time_changed.trim();

                        date_of_order_text_view.setText(date_shown);

                        delivery_phone_number_text.setText(delivery_person_mobile_number);
                        delivery_person_name_text.setText(delivery_person_name);



                        Locale locale = new Locale("hi", "IN");
                        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);


                        String paid_via = "Paid : " + payment_type;
                        paid_via_text.setText(paid_via);

                        phone_number_text.setText(patient_mobile_number);
                        patient_name_text.setText(patient_name);
                        complete_delivery_address_text.setText(delivery_complete_address);


                        status_text.setText(order_status);

                        if (order_status.equalsIgnoreCase("Delivered")){
                            expected_delivery_heading_text.setText("DELIVERED ON");
                            String delivery_date_change_format = getDateFormatChange(delivery_date);
                            String[] delivery_time_list = delivery_time.split(" ");
                            String delivery_time_changed = delivery_time_list[0] + " " + delivery_time_list[1].toUpperCase();

                            String delivery_date_shown = delivery_date_change_format.trim() + ", " + delivery_time_changed.trim();

                            expected_delivery_text.setText(delivery_date_shown);
                            earning_heading_text.setText("YOU EARNED");

                        }

                    }

                    latch.countDown();

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
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void getOrderItem(final String order_number) {

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
                            String quantity = order_item.getString("quantity");
                            String price = order_item.getString("price");
                            String item_status = order_item.getString("order_status");


                            order_list.add(new CartItem(medicine_name, quantity, price, package_contain, item_status));



                        }
                        float grand_total = 0, commission = 0, earning = 0;
                        for (CartItem item : order_list){
                            grand_total += Float.valueOf(item.getPrice());
                        }
                        Locale locale = new Locale("hi", "IN");
                        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);

                        grand_total_text_view.setText(nf.format(grand_total));
                        commission = grand_total *15/100;
                        String commission_text = "-" + nf.format(commission);
                        commission_text_view.setText(commission_text);

                        earning = grand_total - commission;
                        your_earning_text_view.setText(nf.format(earning));

                        earning_text.setText(nf.format(earning));
                        String order_size_shown = "Your Orders (" + order_list.size() + ")";
                        order_size.setText(order_size_shown);
                        OrderListAdapter adapter = new OrderListAdapter(order_list, getApplicationContext(), OrderDetailsActivity.this);
                        order_list_recycler.setAdapter(adapter);
                        order_list_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        order_list_recycler.setHasFixedSize(true);
                    }

                    latch.countDown();

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



    private void getAttachPrescription(final String order_number) {
        String url = "http://prescryp.com/prescriptionUpload/getAttachedPrescription.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    JSONArray jsonArray = jsonObject.getJSONArray("attached_prescription");
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")){
                        for (int i = 0; i < jsonArray.length(); i++) {

                            //getting product object from json array
                            JSONObject attached_prescription = jsonArray.getJSONObject(i);
                            attached_prescription_list.add(new ListItem(attached_prescription.getString("prescription_id"),
                                    attached_prescription.getString("dateOfCreation"), attached_prescription.getString("status"), attached_prescription.getString("imagePath")));

                            pres_verification_done = !attached_prescription.getString("prescription_verification").equalsIgnoreCase("");

                        }
                        String attached_pres_size = "Attached Prescription (" + attached_prescription_list.size() + ")";
                        attached_prescription_number.setText(attached_pres_size);
                        PrescriptionForOrderAdapter adapter = new PrescriptionForOrderAdapter(attached_prescription_list, getApplicationContext(), order_number, patient_mobile_number);
                        attached_prescription_list_recycler.setAdapter(adapter);
                        attached_prescription_list_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        attached_prescription_list_recycler.setHasFixedSize(true);



                    }else if (success.equalsIgnoreCase("2")){
                        attachedPrescriptionCard.setVisibility(View.GONE);
                        pres_verification_done = true;
                    }

                    latch.countDown();

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
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }




}
