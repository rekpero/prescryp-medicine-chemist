package com.prescyber.prescryp.chemists;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prescyber.prescryp.chemists.Model.DailyOrderItem;
import com.prescyber.prescryp.chemists.SessionManager.UserSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserSettingActivity extends AppCompatActivity {

    private TextView store_name_text, store_contact_number_text, store_address_text;
    private ConstraintLayout logOutAction, accountSettings, owner_details, store_details, notificationSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
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


        store_name_text = findViewById(R.id.store_name_text);
        store_contact_number_text = findViewById(R.id.store_contact_number_text);
        store_address_text = findViewById(R.id.store_address_text);
        logOutAction = findViewById(R.id.logOutAction);
        accountSettings = findViewById(R.id.accountSettings);
        owner_details = findViewById(R.id.owner_details);
        store_details = findViewById(R.id.store_details);
        notificationSettings = findViewById(R.id.notificationSettings);

        getStoreDetails();

        logOutAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog();
            }
        });

        accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserSettingActivity.this, AccountSettingsActivity.class));
            }
        });

        owner_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserSettingActivity.this, OwnerDetailsActivity.class));
            }
        });

        store_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserSettingActivity.this, StoreDetailsActivity.class));
            }
        });

        notificationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserSettingActivity.this, NotificationSettingsActivity.class));
            }
        });


    }

    public void logoutDialog(){


        final AlertDialog.Builder builder = new AlertDialog.Builder(UserSettingActivity.this);

        builder.setTitle("Log Out");
        builder.setMessage("Do you want to log out?");

        builder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LogOut();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
        builder.show();
    }

    private void LogOut(){
        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        userSessionManager.logoutUser();
    }

    private void getStoreDetails() {
        UserSessionManager userSessionManager = new UserSessionManager(UserSettingActivity.this);
        HashMap<String, String> user = userSessionManager.getUserDetails();
        final String session_mob = user.get(UserSessionManager.KEY_MOB);
        String url =  "http://prescryp.com/prescriptionUpload/getStoreFront.php";
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

                                String store_name = jsonObject.getString("store_name");
                                String store_contact = jsonObject.getString("store_contact");
                                String store_address = jsonObject.getString("store_address");

                                store_name_text.setText(store_name);
                                store_contact_number_text.setText(store_contact);
                                store_address_text.setText(store_address);

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

        Volley.newRequestQueue(UserSettingActivity.this).add(stringRequest);
    }

}
