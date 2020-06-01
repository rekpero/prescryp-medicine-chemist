package com.prescyber.prescryp.chemists;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.prescyber.prescryp.chemists.SessionManager.UserSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SigninActivity extends AppCompatActivity {



    TextInputLayout mob_num, pass_word;
    ProgressBar progressBar;
    RelativeLayout signin, create_acc;
    String session_mob;
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);



        session = new UserSessionManager(getApplicationContext());
        if (session.isLoggedIn()){
            HashMap<String, String> user = session.getUserDetails();
            String session_mob = user.get(UserSessionManager.KEY_MOB);
            Intent custom = new Intent(SigninActivity.this, MainActivity.class);
            custom.putExtra("SENDERS_KEY", "CUSTOM_LOGIN");
            custom.putExtra("mobile_number", session_mob);
            custom.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(custom);
            finish();
        }

        mob_num = findViewById(R.id.mobnumSignin);
        pass_word = findViewById(R.id.passwordSignin);

        signin = findViewById(R.id.signinCardview);
        create_acc = findViewById(R.id.create_new_acc_button);
        progressBar = findViewById(R.id.loading);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(mob_num.getEditText().getText().toString().trim())) {
                    mob_num.setErrorEnabled(false);
                    mob_num.setError("Cant be blank");

                    progressBar.setVisibility(View.GONE);
                    signin.setVisibility(View.VISIBLE);
                }else if (TextUtils.isEmpty(pass_word.getEditText().getText().toString().trim())){
                    pass_word.setErrorEnabled(false);
                    pass_word.setError("Cant be blank");

                    progressBar.setVisibility(View.GONE);
                    signin.setVisibility(View.VISIBLE);
                }else {

                    signingIn(mob_num.getEditText().getText().toString().trim(), pass_word.getEditText().getText().toString().trim());
                }

            }
        });

        create_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendAcc = new Intent(SigninActivity.this, CreateAccountActivity.class);
                startActivity(sendAcc);
                finish();
            }
        });


    }

    private void signingIn(final String mobnum, final String password) {

        String url = "http://prescryp.com/prescriptionUpload/loginStore.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    JSONArray jsonArray = jsonObject.getJSONArray("login");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if (success.equals("1")){
                        JSONObject object = jsonArray.getJSONObject(0);

                        String name = object.getString("name").trim();

                        session = new UserSessionManager(getApplicationContext());
                        session.createUserLoginSession(name, mobnum, password);
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( SigninActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                String newToken = instanceIdResult.getToken();
                                sendRegistrationToServer(newToken);

                            }
                        });

                    }else {
                        signin.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        mob_num.getEditText().getText().clear();
                        pass_word.getEditText().getText().clear();
                        ConstraintLayout layout = findViewById(R.id.signin);
                        layout.clearFocus();
                        mob_num.requestFocus();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("mobilenumber", mobnum);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

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

                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            //int length = jsonArray.length();
                            //traversing through all the object
                            if (success.equals("1") || success.equals("3")){
                                Intent main = new Intent(SigninActivity.this, MainActivity.class);
                                main.putExtra("SENDERS_KEY", "SIGN_IN");
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



}
