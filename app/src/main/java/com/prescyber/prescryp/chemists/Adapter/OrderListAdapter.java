package com.prescyber.prescryp.chemists.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.prescyber.prescryp.chemists.Model.CartItem;
import com.prescyber.prescryp.chemists.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private List<CartItem> listItems;
    private Context context;
    private Activity activity;

    public OrderListAdapter(List<CartItem> listItems, Context context, Activity activity) {
        this.listItems = listItems;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_status_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final CartItem listItem = listItems.get(position);

        holder.medicineName.setText(listItem.getMedicineName());
        holder.quantity.setText(listItem.getQuantity());
        holder.package_contain.setText(listItem.getPackageContain());
        Locale locale = new Locale("hi", "IN");
        final NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        String totalMrp = nf.format(Integer.valueOf(listItem.getQuantity()) * Float.valueOf(listItem.getPrice()));
        holder.medicine_mrp.setText(totalMrp);

        if (listItem.getItemStatus().equalsIgnoreCase("Rejected")){
            holder.confirmed_order.setVisibility(View.GONE);
            holder.rejected_order.setVisibility(View.VISIBLE);
        }else if (listItem.getItemStatus().equalsIgnoreCase("Confirmed")){
            holder.rejected_order.setVisibility(View.GONE);
            holder.confirmed_order.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView medicineName, package_contain, medicine_mrp, quantity;
        public ConstraintLayout confirmed_order, rejected_order;


        public ViewHolder(View itemview){
            super(itemview);
            medicineName = itemview.findViewById(R.id.medicine_name_order);
            package_contain = itemview.findViewById(R.id.package_contain);
            medicine_mrp = itemview.findViewById(R.id.medicine_mrp);
            quantity = itemview.findViewById(R.id.quantity);
            confirmed_order = itemview.findViewById(R.id.confirmed_order);
            rejected_order = itemview.findViewById(R.id.rejected_order);
        }
    }
}
