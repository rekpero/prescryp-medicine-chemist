package com.prescyber.prescryp.chemists.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prescyber.prescryp.chemists.Model.RejectMedicineItem;
import com.prescyber.prescryp.chemists.R;

import java.util.List;

public class RejectMedicineAdapter extends RecyclerView.Adapter<RejectMedicineAdapter.ViewHolder>{

    private List<RejectMedicineItem> listItems;
    private Context context;

    public RejectMedicineAdapter(List<RejectMedicineItem> listItems, Context context){
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reject_medicines_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RejectMedicineItem listItem = listItems.get(position);

        holder.medName.setText(listItem.getMedicineName());
        holder.medComposition.setText(listItem.getMedicineContains());

        if (listItem.isSelected()){
            holder.empty_check.setVisibility(View.GONE);
            holder.mark_check.setVisibility(View.VISIBLE);
        }else {
            holder.mark_check.setVisibility(View.GONE);
            holder.empty_check.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listItem.isSelected()){
                    listItem.setSelected(false);
                }else {
                    listItem.setSelected(true);
                }

                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView medName, medComposition;
        public ImageView empty_check, mark_check;

        public ViewHolder(View itemview){
            super(itemview);
            medName = itemview.findViewById(R.id.medName);
            medComposition = itemview.findViewById(R.id.medComposition);
            empty_check = itemview.findViewById(R.id.empty_check);
            mark_check = itemview.findViewById(R.id.mark_check);

        }
    }
}
