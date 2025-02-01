package com.vivo.vivorajonboarding;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vivo.vivorajonboarding.adapter.NotificationAdapter;
import com.vivo.vivorajonboarding.common.NetworkChangeListener;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.model.NotificationModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {


    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    //views
    private ImageButton backBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView notificationRv;
    private CardView noDataCardView;

    //Notification Variables
    private NotificationAdapter notificationAdapter;
    ArrayList<NotificationModel> notificationList = new ArrayList<>();

    //Loading Dialog
    private LoadingDialog loadingDialog;

    //Required Variables
    String userid = "", category = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initialization();

        //getIntentData
        getIntentData();

        //handle back button click
        backBtn.setOnClickListener(view -> onBackPressed());

        //load all notification
        loadAllNotification();

        //swipe refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //load all notification
            loadAllNotification();
            swipeRefreshLayout.setRefreshing(false);
        });


    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            userid = getIntent().getStringExtra("userid");
            category = getIntent().getStringExtra("category");
        }
    }

    private void loadAllNotification() {
        loadingDialog.showDialog("Loading...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_ALL_NOTIFICATION_DATA_URL, response -> {
            loadingDialog.hideDialog();
            Log.d("NOTIFICATION RESPONSE", response.trim());
            try {
                notificationList.clear();
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");

                if (status.equalsIgnoreCase("true")) {
                    JSONArray jsonArray = new JSONArray(message);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        NotificationModel notificationModel = new NotificationModel();
                        notificationModel.setNotificationId(object.getString("notificationId"));
                        notificationModel.setNotificationTitle(object.getString("notificationTitle"));
                        notificationModel.setNotificationDate(object.getString("notificationDate"));
                        notificationModel.setNotificationText(object.getString("notificationText"));
                        notificationList.add(notificationModel);
                    }

                    if (notificationList.size() > 0) {
                        noDataCardView.setVisibility(View.GONE);
                        notificationRv.setVisibility(View.VISIBLE);
                        notificationAdapter = new NotificationAdapter(NotificationActivity.this, notificationList);
                        notificationRv.setAdapter(notificationAdapter);
                        notificationAdapter.notifyDataSetChanged();
                    } else {
                        noDataCardView.setVisibility(View.VISIBLE);
                        notificationRv.setVisibility(View.GONE);
                    }
                } else {
                    showToast(message);
                    noDataCardView.setVisibility(View.VISIBLE);
                    notificationRv.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
                showToast(e.toString());
            }
        }, error -> {
            loadingDialog.hideDialog();
            showToast(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", userid);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 30000;
            }

            @Override
            public void retry(VolleyError error) {
                Log.e("RETRY ERROR", error.toString());
                showToast(error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(NotificationActivity.this);
        requestQueue.add(stringRequest);
    }

    private void showToast(String message) {
        Toast.makeText(NotificationActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    private void initialization() {
        backBtn = findViewById(R.id.backBtn);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        notificationRv = findViewById(R.id.notificationRv);
        noDataCardView = findViewById(R.id.noDataCardView);

        loadingDialog = new LoadingDialog(NotificationActivity.this);
    }

}