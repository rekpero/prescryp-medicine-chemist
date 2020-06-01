package com.prescyber.prescryp.chemists;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.prescyber.prescryp.chemists.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddStoreActivity extends AppCompatActivity {

    private TextInputLayout store_name, store_contact, store_address, store_gstin;
    private ProgressBar loading;
    private CardView mark_address;
    private String mobile_number, senders_key;
    private CheckBox confirm_checkbox;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store);

        store_name = findViewById(R.id.store_name);
        store_contact = findViewById(R.id.store_contact_number);
        store_address = findViewById(R.id.store_address);
        store_gstin = findViewById(R.id.store_gstin);

        loading = findViewById(R.id.loading);
        mark_address = findViewById(R.id.mark_address);

        confirm_checkbox = findViewById(R.id.get_confirm_checkbox);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddStoreActivity.this, CreateAccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        if (getIntent() != null){
            mobile_number = getIntent().getStringExtra("mobile_number");
            senders_key = getIntent().getStringExtra("SENDERS_KEY");
        }

        mark_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mark_address.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(store_name.getEditText().getText().toString().trim())){
                    store_name.setErrorEnabled(false);
                    store_name.setError("Cant be blank");
                    loading.setVisibility(View.GONE);
                    mark_address.setVisibility(View.VISIBLE);
                }else if (TextUtils.isEmpty(store_contact.getEditText().getText().toString().trim())){
                    store_contact.setErrorEnabled(false);
                    store_contact.setError("Cant be blank");
                    loading.setVisibility(View.GONE);
                    mark_address.setVisibility(View.VISIBLE);
                }else if (TextUtils.isEmpty(store_address.getEditText().getText().toString().trim())){
                    store_address.setErrorEnabled(false);
                    store_address.setError("Cant be blank");
                    loading.setVisibility(View.GONE);
                    mark_address.setVisibility(View.VISIBLE);
                }
                else if (TextUtils.isEmpty(store_gstin.getEditText().getText().toString().trim())){
                    store_gstin.setErrorEnabled(false);
                    store_gstin.setError("Cant be blank");
                    loading.setVisibility(View.GONE);
                    mark_address.setVisibility(View.VISIBLE);
                }else if (!confirm_checkbox.isChecked()){
                    Toast.makeText(getApplicationContext(), "Agree to our Terms & Condition", Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);
                    mark_address.setVisibility(View.VISIBLE);
                }else {
                    final User user = new User("", mobile_number, "",store_name.getEditText().getText().toString(),
                            store_contact.getEditText().getText().toString(), store_address.getEditText().getText().toString(),
                            store_gstin.getEditText().getText().toString(), "", "");

                    final String url = "http://prescryp.com/prescriptionUpload/addStoreDetails.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                if (success.equalsIgnoreCase("1")){
                                    Intent main = new Intent(AddStoreActivity.this, GetStoreAddressMapActivity.class);
                                    main.putExtra("SENDERS_KEY", senders_key);
                                    main.putExtra("mobile_number", user.getMobileNumber());
                                    main.putExtra("store_address", user.getStoreAddress());
                                    startActivity(main);
                                    finish();
                                }else if (success.equalsIgnoreCase("2")){
                                    store_name.getEditText().getText().clear();
                                    store_contact.getEditText().getText().clear();
                                    store_address.getEditText().getText().clear();
                                    store_gstin.getEditText().getText().clear();
                                    ConstraintLayout layout = findViewById(R.id.add_your_store_layout);
                                    layout.clearFocus();
                                    store_name.requestFocus();

                                    mark_address.setVisibility(View.VISIBLE);
                                    loading.setVisibility(View.INVISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("mobilenumber", user.getMobileNumber());
                            params.put("store_name", user.getStoreName());
                            params.put("store_contact", user.getStoreContact());
                            params.put("store_address", user.getStoreAddress());
                            params.put("store_gstin", user.getStoreGSTIN());
                            return params;
                        }
                    };

                    Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddStoreActivity.this, CreateAccountActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
