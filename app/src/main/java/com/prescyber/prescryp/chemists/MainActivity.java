package com.prescyber.prescryp.chemists;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.prescyber.prescryp.chemists.Misc.Converter;
import com.prescyber.prescryp.chemists.Model.NotificationItem;
import com.prescyber.prescryp.chemists.SessionManager.UserSessionManager;
import com.prescyber.prescryp.chemists.database.NotificationsDBHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout order_management, stock_management, sales_analytics, order_history, add_inventory_layout, user_settings;
    private List<NotificationItem> notificationItem;
    private static int notification_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stock_management = findViewById(R.id.stock_management_layout);
        stock_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, YourStoreItemsActivity.class));
            }
        });

        add_inventory_layout = findViewById(R.id.add_inventory_layout);
        add_inventory_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddItemsInStoreActivity.class));
            }
        });

        order_management = findViewById(R.id.order_management_layout);
        order_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OrderManagementActivity.class));
            }
        });



        order_history = findViewById(R.id.order_history_layout);
        order_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
            }
        });

        sales_analytics = findViewById(R.id.data_analytics_layout);
        sales_analytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AnalyticsActivity.class));
            }
        });

        user_settings = findViewById(R.id.setting_layout);
        user_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserSettingActivity.class));
            }
        });

        notificationItem = new NotificationsDBHelper(this).getNotificationList();
        notification_count = notificationItem.size();
        invalidateOptionsMenu();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        notificationItem = new NotificationsDBHelper(this).getNotificationList();
        notification_count = notificationItem.size();

        invalidateOptionsMenu();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the notifications MenuItem and LayerDrawable (layer-list)
        MenuItem notification = menu.findItem(R.id.action_notification);
        notification.setIcon(Converter.convertLayoutToImage(MainActivity.this, notification_count, R.mipmap.ic_bell_white_icon));
        notification.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                return true;
            }
        });


        return true;
    }


}
