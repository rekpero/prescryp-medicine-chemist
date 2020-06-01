package com.prescyber.prescryp.chemists;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NotificationSettingsActivity extends AppCompatActivity {

    Switch enableAllSwitch;
    CheckBox activity_photo_checkbox, important_updates_checkbox, social_notification_checkbox, activity_photo_mobile_checkbox, important_updates_mobile_checkbox, social_notification_mobile_checkbox;
    Boolean isUnchecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        activity_photo_checkbox = findViewById(R.id.activity_photo_checkbox);
        important_updates_checkbox = findViewById(R.id.important_updates_checkbox);
        social_notification_checkbox = findViewById(R.id.social_notification_checkbox);
        activity_photo_mobile_checkbox = findViewById(R.id.activity_photo_mobile_checkbox);
        important_updates_mobile_checkbox = findViewById(R.id.important_updates_mobile_checkbox);
        social_notification_mobile_checkbox = findViewById(R.id.social_notification_mobile_checkbox);

        enableAllSwitch = findViewById(R.id.enableAllSwitch);
        enableAllSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    activity_photo_checkbox.setChecked(true);
                    important_updates_checkbox.setChecked(true);
                    social_notification_checkbox.setChecked(true);
                    activity_photo_mobile_checkbox.setChecked(true);
                    important_updates_mobile_checkbox.setChecked(true);
                    social_notification_mobile_checkbox.setChecked(true);
                } else {
                    if (isUnchecked == false) {
                        activity_photo_checkbox.setChecked(false);
                        important_updates_checkbox.setChecked(false);
                        social_notification_checkbox.setChecked(false);
                        activity_photo_mobile_checkbox.setChecked(false);
                        important_updates_mobile_checkbox.setChecked(false);
                        social_notification_mobile_checkbox.setChecked(false);
                    }
                    isUnchecked = false;

                }
            }
        });

        activity_photo_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == false){
                    isUnchecked = true;
                    enableAllSwitch.setChecked(false);
                }
            }
        });
        important_updates_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == false){
                    isUnchecked = true;
                    enableAllSwitch.setChecked(false);
                }
            }
        });
        social_notification_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == false){
                    isUnchecked = true;
                    enableAllSwitch.setChecked(false);
                }
            }
        });
        activity_photo_mobile_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == false){
                    isUnchecked = true;
                    enableAllSwitch.setChecked(false);
                }
            }
        });
        important_updates_mobile_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == false){
                    isUnchecked = true;
                    enableAllSwitch.setChecked(false);

                }
            }
        });
        social_notification_mobile_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == false){
                    isUnchecked = true;
                    enableAllSwitch.setChecked(false);
                }
            }
        });

    }

}
