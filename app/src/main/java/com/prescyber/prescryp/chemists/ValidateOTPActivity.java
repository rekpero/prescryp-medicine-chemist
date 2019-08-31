package com.prescyber.prescryp.chemists;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaos.view.PinView;

public class ValidateOTPActivity extends AppCompatActivity {

    private PinView otp_pinview;
    private String mobile_number, senders_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_otp);

        TextView mobile_text = findViewById(R.id.mobile_number_text);
        otp_pinview = findViewById(R.id.quantity);
        ImageView next_activity = findViewById(R.id.nextActivity);
        ImageView back_activity = findViewById(R.id.backActivity);


        otp_pinview.setAnimationEnable(true);

        if (getIntent() != null){
            mobile_number = getIntent().getStringExtra("mobile_number");
            senders_key = getIntent().getStringExtra("SENDERS_KEY");
        }

        String mobile_num_text = "(+91) " + mobile_number;
        mobile_text.setText(mobile_num_text);

        back_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });


        next_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = otp_pinview.getText().toString();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                if (text.length() == 6){
                    if (senders_key.equalsIgnoreCase("SIGN_UP")){
                        Intent intent = new Intent(ValidateOTPActivity.this, AddStoreActivity.class);
                        intent.putExtra("SENDERS_KEY", senders_key);
                        intent.putExtra("mobile_number", mobile_number);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }else if (senders_key.equalsIgnoreCase("SIGN_IN")){
                        Intent intent = new Intent(ValidateOTPActivity.this, MainActivity.class);
                        intent.putExtra("SENDERS_KEY", senders_key);
                        intent.putExtra("mobile_number", mobile_number);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }


                }

            }
        });


    }

}
