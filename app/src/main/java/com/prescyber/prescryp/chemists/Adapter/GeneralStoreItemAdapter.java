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
import android.widget.ImageView;
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
import com.prescyber.prescryp.chemists.Model.GeneralStoreItem;
import com.prescyber.prescryp.chemists.Model.StoreMedicineItem;
import com.prescyber.prescryp.chemists.R;
import com.prescyber.prescryp.chemists.SessionManager.UserSessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralStoreItemAdapter extends RecyclerView.Adapter<GeneralStoreItemAdapter.ViewHolder>{

    private List<GeneralStoreItem> listItems;
    private Context context;
    private OnBottomReachedListener onBottomReachedListener;
    private OnNotReachedBottomListener onNotReachedBottomListener;

    public GeneralStoreItemAdapter(List<GeneralStoreItem> listItems, Context context){
        this.listItems = listItems;
        this.context = context;
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){

        this.onBottomReachedListener = onBottomReachedListener;
    }

    public void setOnNotReachedBottomListener(OnNotReachedBottomListener onNotReachedBottomListener){

        this.onNotReachedBottomListener = onNotReachedBottomListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.general_item_to_add_in_store_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final GeneralStoreItem item = listItems.get(position);

        if (position == listItems.size() - 1){

            onBottomReachedListener.onBottomReached(position);

        }
        if (position != listItems.size() - 1){
            onNotReachedBottomListener.onNotReachedBottom(position);
        }

        holder.item_name.setText(item.getName());
        holder.upper_category.setText(item.getUpperCategory());
        holder.category.setText(item.getCategory());
        holder.title.setText(item.getTitle());
        if (item.getQuantity() != 0){
            holder.quantity.setText(String.valueOf(item.getQuantity()));
        }else {
            holder.quantity.setText("");
        }
        if (item.getPrice() != 0){
            holder.price.setText(String.valueOf(item.getPrice()));
        }else {
            holder.price.setText("");
        }
        Picasso.get()
                .load(item.getImageUrl())
                .fit()
                .into(holder.item_image);

        holder.checkAdded.setText(item.getCheckAdded());

        holder.addMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = item.getName();
                String itemQty = holder.quantity.getText().toString();
                String itemPrice = holder.price.getText().toString();
                String checkAdded = item.getCheckAdded();
                String id = item.getId();
                addMedicineToStore(id, itemName, itemQty, itemPrice, checkAdded, position);
            }
        });

    }

    private void addMedicineToStore(final String id, final String itemName, final String itemQty, final String itemPrice, final String checkAdded, final int position) {
        UserSessionManager userSessionManager = new UserSessionManager(context);
        HashMap<String, String> user = userSessionManager.getUserDetails();
        final String session_mob = user.get(UserSessionManager.KEY_MOB);
        String url =  "http://prescryp.com/prescriptionUpload/addGeneralStoreItem.php";
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
                                    SharedPreferences sp = context.getSharedPreferences("store_general_item_max_id", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putInt("max_general_item_id_key", Integer.parseInt(id));
                                    editor.apply();

                                    listItems.remove(position);
                                }else {
                                    listItems.get(position).setQuantity(Integer.parseInt(itemQty));
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
                params.put("store_item_name", itemName);
                params.put("store_item_qty", itemQty);
                params.put("store_item_price", itemPrice);
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

        private TextView item_name, upper_category, category, title, checkAdded;
        private EditText quantity, price;
        private ConstraintLayout addMedicine;
        private ImageView item_image;

        public ViewHolder(View itemview){
            super(itemview);

            item_name = itemview.findViewById(R.id.item_name);
            upper_category = itemview.findViewById(R.id.upper_category);
            category = itemview.findViewById(R.id.category);
            title = itemview.findViewById(R.id.title);
            quantity = itemview.findViewById(R.id.quantity);
            price = itemview.findViewById(R.id.price);
            addMedicine = itemview.findViewById(R.id.addMedicine);
            item_image = itemview.findViewById(R.id.item_image);
            checkAdded = itemview.findViewById(R.id.checkAdded);

        }
    }
}
