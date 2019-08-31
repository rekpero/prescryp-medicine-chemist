package com.prescyber.prescryp.chemists;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.prescyber.prescryp.chemists.Model.User;
import com.prescyber.prescryp.chemists.SessionManager.UserSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {



    TextInputLayout name, mob_num, password;
    ProgressBar progressBar;
    CardView add_your_store_btn;
    RelativeLayout signin_now;
    UserSessionManager session;
    String session_mob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


        session = new UserSessionManager(getApplicationContext());


        name = findViewById(R.id.full_name);
        mob_num = findViewById(R.id.mobile_number_text);
        password = findViewById(R.id.password_text);

        add_your_store_btn = findViewById(R.id.add_your_store);

        signin_now = findViewById(R.id.signin_now);
        signin_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent si = new Intent(CreateAccountActivity.this, SigninActivity.class);
                startActivity(si);
                finish();
            }
        });
        progressBar = findViewById(R.id.loading);
        add_your_store_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_your_store_btn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(name.getEditText().getText().toString().trim())){
                    name.setErrorEnabled(false);
                    name.setError("Cant be blank");
                    progressBar.setVisibility(View.GONE);
                    add_your_store_btn.setVisibility(View.VISIBLE);
                }else if (TextUtils.isEmpty(mob_num.getEditText().getText().toString().trim())){
                    mob_num.setErrorEnabled(false);
                    mob_num.setError("Cant be blank");
                    progressBar.setVisibility(View.GONE);
                    add_your_store_btn.setVisibility(View.VISIBLE);
                }else if (TextUtils.isEmpty(password.getEditText().getText().toString().trim())){
                    password.setErrorEnabled(false);
                    password.setError("Cant be blank");
                    progressBar.setVisibility(View.GONE);
                    add_your_store_btn.setVisibility(View.VISIBLE);
                }
                else {
                    final User user = new User(name.getEditText().getText().toString().trim(),
                            mob_num.getEditText().getText().toString().trim(),password.getEditText().getText().toString().trim(), "",
                            "", "", "", "", "");

                    final String url = "http://prescryp.com/prescriptionUpload/createStoreAccount.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");

                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                if (success.equalsIgnoreCase("1")){
                                    session = new UserSessionManager(getApplicationContext());
                                    session.createUserLoginSession(user.getName(), user.getMobileNumber(), user.getPassword());
                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( CreateAccountActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            String newToken = instanceIdResult.getToken();
                                            sendRegistrationToServer(newToken);

                                        }
                                    });
                                }else if (success.equalsIgnoreCase("2")){
                                    name.getEditText().getText().clear();
                                    mob_num.getEditText().getText().clear();
                                    password.getEditText().getText().clear();
                                    ConstraintLayout layout = findViewById(R.id.create_new_acc);
                                    layout.clearFocus();
                                    name.requestFocus();

                                    add_your_store_btn.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
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
                            params.put("mobile_number", user.getMobileNumber());
                            params.put("name", user.getName());
                            params.put("password", user.getPassword());
                            return params;
                        }
                    };

                    Volley.newRequestQueue(getApplicationContext()).add(stringRequest);


                }

            }
        });


    }

    private void sendRegistrationToServer(final String token) {
        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        final HashMap<String, String> user = userSessionManager.getUserDetails();
        session_mob = user.get(UserSessionManager.KEY_MOB);
        String url =  "http://prescryp.com/prescriptionUpload/insertToken.php";
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
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            if (success.equals("1") || success.equals("3")){
                                Intent main = new Intent(CreateAccountActivity.this, AddStoreActivity.class);
                                main.putExtra("SENDERS_KEY", "SIGN_UP");
                                main.putExtra("mobile_number", session_mob);
                                startActivity(main);
                                finish();


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
                params.put("token", token);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
