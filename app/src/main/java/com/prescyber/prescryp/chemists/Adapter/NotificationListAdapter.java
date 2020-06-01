package com.prescyber.prescryp.chemists.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.prescyber.prescryp.chemists.Model.NotificationItem;
import com.prescyber.prescryp.chemists.R;

import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    private List<NotificationItem> listItems;
    private Context context;

    public NotificationListAdapter(List<NotificationItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final NotificationItem listItem = listItems.get(position);

        holder.notification_title.setText(listItem.getTitle());
        holder.notification_message.setText(listItem.getDescription());
        holder.date_of_notification.setText(listItem.getDate());


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView notification_title, notification_message, date_of_notification;
        public ConstraintLayout NotificationLayout;


        public ViewHolder(View itemview){
            super(itemview);
            notification_title = itemview.findViewById(R.id.notification_title);
            notification_message = itemview.findViewById(R.id.notification_message);
            date_of_notification = itemview.findViewById(R.id.date_of_notification);
            NotificationLayout = itemview.findViewById(R.id.NotificationLayout);
        }
    }
}
