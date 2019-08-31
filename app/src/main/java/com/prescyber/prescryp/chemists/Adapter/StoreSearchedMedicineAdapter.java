package com.prescyber.prescryp.chemists.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prescyber.prescryp.chemists.Interface.OnBottomReachedListener;
import com.prescyber.prescryp.chemists.Interface.OnNotReachedBottomListener;
import com.prescyber.prescryp.chemists.Model.StoreMedicineItem;
import com.prescyber.prescryp.chemists.R;
import com.prescyber.prescryp.chemists.SessionManager.UserSessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreSearchedMedicineAdapter extends RecyclerView.Adapter<StoreSearchedMedicineAdapter.ViewHolder>{

    private List<StoreMedicineItem> listItems;
    private Context context;

    public StoreSearchedMedicineAdapter(List<StoreMedicineItem> listItems, Context context){
        this.listItems = listItems;
        this.context = context;
    }



    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.med_to_add_in_store_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final StoreMedicineItem medicineItem = listItems.get(position);

        holder.medicineName.setText(medicineItem.getMedicineName());
        holder.companyName.setText(medicineItem.getCompanyName());
        holder.form.setText(medicineItem.getForm());
        holder.packaging.setText(medicineItem.getPackaging());
        if (medicineItem.getQuantity() != 0){
            holder.quantity.setText(String.valueOf(medicineItem.getQuantity()));
        }else {
            holder.quantity.setText("");
        }
        if (medicineItem.getCheckPrescription().equalsIgnoreCase("Yes")){
            holder.requirePrescription.setChecked(true);
        }else {
            holder.requirePrescription.setChecked(false);
        }
        holder.checkAdded.setText(medicineItem.getCheckAdded());

        holder.addMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String medicineName = medicineItem.getMedicineName();
                String medicinePackaging = medicineItem.getPackaging();
                String medicineQty = holder.quantity.getText().toString();
                String requirePres;
                if (holder.requirePrescription.isChecked()){
                    requirePres = "Yes";
                }else {
                    requirePres = "No";
                }
                String checkAdded = medicineItem.getCheckAdded();
                String id = medicineItem.getId();
                addMedicineToStore(id, medicineName, medicinePackaging, medicineQty, requirePres, checkAdded, position);
            }
        });

    }

    private void addMedicineToStore(final String id, final String medicineName, final String medicinePackaging, final String medicineQty, final String requirePres, final String checkAdded, final int position) {
        UserSessionManager userSessionManager = new UserSessionManager(context);
        HashMap<String, String> user = userSessionManager.getUserDetails();
        final String session_mob = user.get(UserSessionManager.KEY_MOB);
        String url =  "http://prescryp.com/prescriptionUpload/addMedicineInStore.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting the string to json array object
                            //JSONArray array = new JSONArray(response);
                            JSONObject jsonObject = new JSONObject(response);
                            String success1 = jsonObject.getString("success1");
                            String message1 = jsonObject.getString("message1");
                            String exists1 = jsonObject.getString("exists1");

                            String success2 = jsonObject.getString("success2");
                            String message2 = jsonObject.getString("message2");
                            String exists2 = jsonObject.getString("exists2");

                            Toast.makeText(context, message1, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context, message2, Toast.LENGTH_SHORT).show();

                            Log.e("STORE MANAGE", "Message1 = " + message1 + " Message2 = " + message2);
                            Log.e("STORE MANAGE", "Exists1 = " + exists1 + " Exists2 = " + exists2);


                            //traversing through all the object
                            if ((success1.equals("1") && success2.equals("1")) || (success1.equals("2") && success2.equals("2"))){
                                if (checkAdded.equalsIgnoreCase("ADD")){
                                    SharedPreferences sp = context.getSharedPreferences("store_max_id", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putInt("max_id_key", Integer.parseInt(id));
                                    editor.apply();
                                    listItems.remove(position);
                                }else {
                                    listItems.get(position).setCheckPrescription(requirePres);
                                }
                                notifyDataSetChanged();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile_number", session_mob);
                params.put("medicine_packaging", medicinePackaging);
                params.put("medicine_name", medicineName);
                params.put("medicine_qty", medicineQty);
                params.put("require_pres", requirePres);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView medicineName;
        public TextView companyName;
        public TextView form;
        public TextView packaging;
        public TextView checkAdded;
        public CardView cardView;
        public ConstraintLayout addMedicine;
        public EditText quantity;
        public CheckBox requirePrescription;

        public ViewHolder(View itemview){
            super(itemview);
            cardView = itemview.findViewById(R.id.medicineCard);
            medicineName = itemview.findViewById(R.id.medicine_name);
            companyName =  itemview.findViewById(R.id.company_name);
            form = itemview.findViewById(R.id.form);
            packaging = itemview.findViewById(R.id.packaging);
            addMedicine = itemview.findViewById(R.id.addMedicine);
            quantity = itemview.findViewById(R.id.quantity);
            requirePrescription = itemview.findViewById(R.id.require_prescription);
            checkAdded = itemview.findViewById(R.id.checkAdded);

        }
    }
}
