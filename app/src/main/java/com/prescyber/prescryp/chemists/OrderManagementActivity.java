package com.prescyber.prescryp.chemists;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prescyber.prescryp.chemists.Adapter.OrderRecievedListAdapter;
import com.prescyber.prescryp.chemists.Model.OrderReceivedItem;
import com.prescyber.prescryp.chemists.SessionManager.UserSessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManagementActivity extends AppCompatActivity {
    private RecyclerView order_received_list;
    private String session_mob;
    private List<OrderReceivedItem> receivedItemsList;
    private TextView received_order_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        order_received_list = findViewById(R.id.order_received_list);
        received_order_text = findViewById(R.id.received_order_text);
        receivedItemsList = new ArrayList<>();
        
        getAllOrderReceived();
        
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        receivedItemsList = new ArrayList<>();
        getAllOrderReceived();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getAllOrderReceived() {
        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = userSessionManager.getUserDetails();
        session_mob = user.get(UserSessionManager.KEY_MOB);
        String url =  "http://prescryp.com/prescriptionUpload/getAllOrderForChemist.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting the string to json array object
                            //JSONArray array = new JSONArray(response);
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            JSONArray jsonArray = jsonObject.getJSONArray("order_history");

                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            //int length = jsonArray.length();
                            //traversing through all the object
                            if (success.equals("1")){
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //getting product object from json array
                                    JSONObject orders = jsonArray.getJSONObject(i);

                                    //adding the product to product list

                                    String order_number = orders.getString("order_number");
                                    String date_of_order = orders.getString("date_of_order");
                                    String time_of_order = orders.getString("time_of_order");
                                    String order_status = orders.getString("order_status");
                                    String patient_name = orders.getString("patient_name");
                                    String medicine_name = orders.getString("medicine_name");
                                    String quantity = orders.getString("quantity");
                                    String price = orders.getString("price");
                                    String order_item = quantity + " \u00D7 " + medicine_name;


                                    if (!order_status.equalsIgnoreCase("Delivered") && !order_status.equalsIgnoreCase("Declined")  && !order_status.equalsIgnoreCase("Rejected")){
                                        boolean contains = false;
                                        for (OrderReceivedItem item : receivedItemsList){
                                            if (item.getOrderNumber().equalsIgnoreCase(order_number)){
                                                item.getOrderItems().add(order_item);
                                                float get_price = Float.valueOf(item.getGrandTotal());
                                                get_price += Float.valueOf(price);
                                                item.setGrandTotal(String.valueOf(get_price));
                                                contains = true;
                                            }
                                        }

                                        if (!contains){
                                            List<String> order_item_list = new ArrayList<>();
                                            order_item_list.add(order_item);
                                            receivedItemsList.add(new OrderReceivedItem(order_number, date_of_order, time_of_order, order_status, patient_name,
                                                    price, order_item_list));
                                        }
                                    }



                                }

                                if (receivedItemsList.size() == 0){
                                    order_received_list.setVisibility(View.GONE);
                                    received_order_text.setVisibility(View.VISIBLE);
                                }
                                OrderRecievedListAdapter adapter = new OrderRecievedListAdapter(receivedItemsList, OrderManagementActivity.this);
                                order_received_list.setAdapter(adapter);
                                order_received_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                order_received_list.setHasFixedSize(true);




                            }else if (success.equals("2")){
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile_number", session_mob);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }


}
