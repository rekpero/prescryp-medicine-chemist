package com.prescyber.prescryp.chemists.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.prescyber.prescryp.chemists.DigitalPrescriptionActivity;
import com.prescyber.prescryp.chemists.Model.ListItem;
import com.prescyber.prescryp.chemists.PrescriptionImageviewActivity;
import com.prescyber.prescryp.chemists.R;

import java.util.List;

public class PrescriptionForOrderAdapter extends RecyclerView.Adapter<PrescriptionForOrderAdapter.ViewHolder>{

    private List<ListItem> listItems;
    private Context context;
    private String order_number;
    private String patient_phone_number;

    public PrescriptionForOrderAdapter(List<ListItem> listItems, Context context, String order_number, String patient_phone_number){
        this.listItems = listItems;
        this.context = context;
        this.order_number = order_number;
        this.patient_phone_number = patient_phone_number;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attached_prescription_order_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ListItem listItem = listItems.get(position);

        holder.textViewPresId.setText(listItem.getPrescriptionId());
        final String status = "Status : " + listItem.getStatus();
        holder.textViewStatus.setText(status);
        holder.presLinearview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, PrescriptionImageviewActivity.class);
                i.putExtra("Sender", "Verification");
                i.putExtra("PrescriptionId", listItem.getPrescriptionId());
                i.putExtra("Date", listItem.getDate());
                i.putExtra("ImagePathUrl", listItem.getImagePath());
                i.putExtra("Status", listItem.getStatus());
                i.putExtra("Order_Number", order_number);
                i.putExtra("Patient_Mobile_number", patient_phone_number);
                i.putExtra("NumberOfPrescription", String.valueOf(listItems.size()));
                context.startActivity(i);
            }
        });
    }



    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewPresId;
        public TextView textViewStatus;
        public LinearLayout presLinearview;

        public ViewHolder(View itemview){
            super(itemview);
            textViewPresId = (TextView) itemview.findViewById(R.id.presId);
            textViewStatus = (TextView) itemview.findViewById(R.id.presstatus);
            presLinearview = (LinearLayout) itemview.findViewById(R.id.presLinearView);

        }
    }
}
