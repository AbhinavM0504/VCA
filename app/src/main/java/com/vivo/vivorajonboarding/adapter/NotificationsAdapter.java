package com.vivo.vivorajonboarding.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.NotificationsModel;

import java.util.List;

// NotificationAdapter.java
public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NOTIFICATION = 1;

    private List<Object> items; // Can contain String (header) or NotificationModel
    private Context context;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationsModel notification, int position);
    }

    public NotificationsAdapter(Context context, List<Object> items, OnNotificationClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_NOTIFICATION;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
            return new NotificationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).headerText.setText((String) items.get(position));
        } else {
            NotificationsModel notification = (NotificationsModel) items.get(position);
            NotificationViewHolder notificationHolder = (NotificationViewHolder) holder;

            notificationHolder.title.setText(notification.getTitle());
            notificationHolder.message.setText(notification.getMessage());
            notificationHolder.time.setText(notification.getTime());
            notificationHolder.icon.setImageResource(notification.getNotificationIcon());

            notificationHolder.unreadIndicator.setVisibility(
                    notification.isRead() ? View.GONE : View.VISIBLE
            );

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationClick(notification, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;

        HeaderViewHolder(View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.header_text);
        }
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView message;
        TextView time;
        View unreadIndicator;

        NotificationViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.notification_icon);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
            time = itemView.findViewById(R.id.notification_time);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
        }
    }
}