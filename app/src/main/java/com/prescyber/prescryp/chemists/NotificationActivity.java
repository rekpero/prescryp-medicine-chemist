package com.prescyber.prescryp.chemists;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.prescyber.prescryp.chemists.Adapter.NotificationListAdapter;
import com.prescyber.prescryp.chemists.Model.NotificationItem;
import com.prescyber.prescryp.chemists.database.NotificationsDBHelper;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView notification_list;
    private TextView no_notification;
    private List<NotificationItem> notificationItemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
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

        notification_list = findViewById(R.id.notification_list);
        no_notification = findViewById(R.id.no_notification);

        notificationItemList = new NotificationsDBHelper(getApplicationContext()).getNotificationList();

        if (notificationItemList.size() == 0){
            no_notification.setVisibility(View.VISIBLE);
        }else {
            no_notification.setVisibility(View.GONE);
        }
        NotificationListAdapter adapter = new NotificationListAdapter(notificationItemList, getApplicationContext());
        notification_list.setAdapter(adapter);
        notification_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

}
