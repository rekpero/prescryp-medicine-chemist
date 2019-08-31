package com.prescyber.prescryp.chemists;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prescyber.prescryp.chemists.SessionManager.UserSessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OwnerDetailsActivity extends AppCompatActivity {

    private TextInputLayout name_input, phone_no_input, email_input;
    private CardView saveProfileUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_details);
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


        name_input = findViewById(R.id.name_input);
        phone_no_input = findViewById(R.id.phone_no_input);
        email_input = findViewById(R.id.email_input);
        saveProfileUpdate = findViewById(R.id.saveProfileUpdate);

        getEmail();

        saveProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmail(email_input.getEditText().getText().toString());
            }
        });


    }

    private void updateEmail(final String email) {
        UserSessionManager userSessionManager = new UserSessionManager(OwnerDetailsActivity.this);
        HashMap<String, String> user = userSessionManager.getUserDetails();
        final String session_mob = user.get(UserSessionManager.KEY_MOB);
        String url =  "http://prescryp.com/prescriptionUpload/updateChemistEmail.php";
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
                            //int length = jsonArray.length();
                            //traversing through all the object
                            if (success.equals("1")){

                                onBackPressed();


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
                params.put("email", email);
                return params;
            }
        };

        Volley.newRequestQueue(OwnerDetailsActivity.this).add(stringRequest);
    }

    private void getEmail() {
        UserSessionManager userSessionManager = new UserSessionManager(OwnerDetailsActivity.this);
        HashMap<String, String> user = userSessionManager.getUserDetails();
        final String session_mob = user.get(UserSessionManager.KEY_MOB);
        String url =  "http://prescryp.com/prescriptionUpload/getEmailChemist.php";
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
                            //int length = jsonArray.length();
                            //traversing through all the object
                            if (success.equals("1")){

                                String name = jsonObject.getString("name");
                                String email = jsonObject.getString("email");
                                name_input.getEditText().setText(name);
                                name_input.getEditText().setEnabled(false);
                                email_input.getEditText().setText(email);
                                phone_no_input.getEditText().setText(session_mob);
                                phone_no_input.getEditText().setEnabled(false);


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
                return params;
            }
        };

        Volley.newRequestQueue(OwnerDetailsActivity.this).add(stringRequest);
    }

}
