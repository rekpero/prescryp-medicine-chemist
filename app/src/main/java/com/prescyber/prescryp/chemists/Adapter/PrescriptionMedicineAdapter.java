package com.prescyber.prescryp.chemists.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prescyber.prescryp.chemists.Model.MedicineListItem;
import com.prescyber.prescryp.chemists.R;

import java.util.List;

public class PrescriptionMedicineAdapter extends RecyclerView.Adapter<PrescriptionMedicineAdapter.ViewHolder>{

    private List<MedicineListItem> listItems;
    private Context context;

    public PrescriptionMedicineAdapter(List<MedicineListItem> listItems, Context context){
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prescription_medicine_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MedicineListItem listItem = listItems.get(position);

        holder.medicineName.setText(listItem.getMedicineName());
        holder.medicineStrength.setText(listItem.getMedicineStrength());
        holder.medicineDose.setText(listItem.getMedicineDose());
        String duration = listItem.getMedicineDuration() + " days";
        holder.medicineDuration.setText(duration);


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView medicineName;
        public TextView medicineStrength;
        public TextView medicineDose;
        public TextView medicineDuration;

        public ViewHolder(View itemview){
            super(itemview);
            medicineName = itemview.findViewById(R.id.medicinesName);
            medicineStrength = itemview.findViewById(R.id.medicineStrength);
            medicineDose = itemview.findViewById(R.id.medicineDose);
            medicineDuration = itemview.findViewById(R.id.medicineDuration);

        }
    }
}
