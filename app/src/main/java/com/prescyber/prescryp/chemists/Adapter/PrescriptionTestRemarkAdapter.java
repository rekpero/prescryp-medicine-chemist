package com.prescyber.prescryp.chemists.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.prescyber.prescryp.chemists.R;

import java.util.List;

public class PrescriptionTestRemarkAdapter extends RecyclerView.Adapter<PrescriptionTestRemarkAdapter.ViewHolder>{

    private List<String> listItems;
    private Context context;

    public PrescriptionTestRemarkAdapter(List<String> listItems, Context context){
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prescription_test_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String listItem = listItems.get(position);

        holder.testName.setText(listItem);

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView testName;

        public ViewHolder(View itemview){
            super(itemview);
            testName = itemview.findViewById(R.id.testName);

        }
    }
}
