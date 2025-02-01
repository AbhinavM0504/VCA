package com.vivo.vivorajonboarding.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.NotificationModel;

import java.util.ArrayList;

public class
NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {


    private Context context;
    private ArrayList<NotificationModel> notificationList;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_items_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notificationModel = notificationList.get(position);

        //set value to fields
        holder.notificationTitleTv.setText(notificationModel.getNotificationTitle());
        holder.notificationDateTv.setText(notificationModel.getNotificationDate());
        holder.notificationTextTv.setText(notificationModel.getNotificationText());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView notificationTitleTv, notificationDateTv, notificationTextTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTitleTv = itemView.findViewById(R.id.notificationTitleTv);
            notificationDateTv = itemView.findViewById(R.id.notificationDateTv);
            notificationTextTv = itemView.findViewById(R.id.notificationTextTv);
        }
    }
}
