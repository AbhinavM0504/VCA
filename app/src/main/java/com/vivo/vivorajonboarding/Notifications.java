package com.vivo.vivorajonboarding;



import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.vivo.vivorajonboarding.adapter.NotificationsAdapter;
import com.vivo.vivorajonboarding.model.NotificationsModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Notifications extends AppCompatActivity  implements NavigationBarView.OnItemSelectedListener {
    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private List<Object> items;
    private View overlayBackground;
    private CardView expandedNotificationCard;
    private BottomNavigationView bottomNavigationView;
    // Constants for gradient colors
    private static final int COLOR_CYAN = 0xFF2B2B2B;
    private static final int COLOR_BLUE = 0xFF2B2B2B;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_one);

        initViews();
        setupRecyclerView();
        loadNotifications();
        setupBottomNavigation();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.notifications_recycler_view);
        overlayBackground = findViewById(R.id.overlay_background);
        expandedNotificationCard = findViewById(R.id.expanded_notification_card);
        overlayBackground.setOnClickListener(v -> hideExpandedNotification());
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupRecyclerView() {
        items = new ArrayList<>();
        adapter = new NotificationsAdapter(this, items, this::showExpandedNotification);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadNotifications() {
        // Create separate lists for today and yesterday
        List<NotificationsModel> todayNotifications = new ArrayList<>();
        List<NotificationsModel> yesterdayNotifications = new ArrayList<>();

        // Today's notifications
        todayNotifications.add(new NotificationsModel(
                "Reminder for your meetings",
                "Learn more about managing account info and activity",
                "9min ago",
                false,
                "Today",
                R.drawable.ic_phone_resized
        ));

        todayNotifications.add(new NotificationsModel(
                "Robert mention you!",
                "Learn more about managing account info and activity",
                "14min ago",
                false,
                "Today",
                R.drawable.ic_phone_resized
        ));

        todayNotifications.add(new NotificationsModel(
                "Reminder for your serial",
                "Learn more about managing account info and activity",
                "25min ago",
                true,
                "Today",
                R.drawable.ic_phone_resized
        ));

        // Yesterday's notifications
        yesterdayNotifications.add(new NotificationsModel(
                "Reminder for your serial",
                "Looking forward to it",
                "22h ago",
                false,
                "Yesterday",
                R.drawable.ic_phone_resized
        ));

        yesterdayNotifications.add(new NotificationsModel(
                "Weekly activity summary",
                "Check out your activity summary for this week",
                "23h ago",
                true,
                "Yesterday",
                R.drawable.ic_phone_resized
        ));

        // Sort notifications by time (newest first)
        Comparator<NotificationsModel> timeComparator = (n1, n2) -> {
            // Convert time strings to comparable values
            int time1 = convertTimeToMinutes(n1.getTime());
            int time2 = convertTimeToMinutes(n2.getTime());
            return time1 - time2;  // Ascending order (newer times are smaller numbers)
        };

        Collections.sort(todayNotifications, timeComparator);
        Collections.sort(yesterdayNotifications, timeComparator);

        // Add to items list in order
        items.clear();
        if (!todayNotifications.isEmpty()) {
            items.add("Today");
            items.addAll(todayNotifications);
        }
        if (!yesterdayNotifications.isEmpty()) {
            items.add("Yesterday");
            items.addAll(yesterdayNotifications);
        }

        adapter.notifyDataSetChanged();
    }

    private int convertTimeToMinutes(String timeString) {
        // Convert time strings to minutes for comparison
        if (timeString.contains("min")) {
            return Integer.parseInt(timeString.split("min")[0].trim());
        } else if (timeString.contains("h")) {
            return Integer.parseInt(timeString.split("h")[0].trim()) * 60;
        }
        return Integer.MAX_VALUE; // For any other format
    }

    private void showExpandedNotification(NotificationsModel notification, int position) {
        TextView expandedTitle = expandedNotificationCard.findViewById(R.id.expanded_title);
        TextView expandedMessage = expandedNotificationCard.findViewById(R.id.expanded_message);
        TextView expandedTime = expandedNotificationCard.findViewById(R.id.expanded_time);

        expandedTitle.setText(notification.getTitle());
        expandedMessage.setText(notification.getMessage());
        expandedTime.setText(notification.getTime());

        overlayBackground.setVisibility(View.VISIBLE);
        expandedNotificationCard.setVisibility(View.VISIBLE);

        // Mark notification as read
        notification.setRead(true);
        adapter.notifyItemChanged(position);
    }

    private void hideExpandedNotification() {
        overlayBackground.setVisibility(View.GONE);
        expandedNotificationCard.setVisibility(View.GONE);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(this);
        setupNavigationColors();
        bottomNavigationView.setSelectedItemId(R.id.nav_notifications); // Change this line
    }

    private void setupNavigationColors() {
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked },
                new int[] { -android.R.attr.state_checked }
        };

        int[] colors = new int[] {
                COLOR_BLUE,
                COLOR_CYAN
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNavigationView.setItemTextColor(colorStateList);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent = null;
        int itemId = item.getItemId();
        Log.d("Navigation", "Selected item ID: " + item.getItemId());

        if (itemId == R.id.nav_home) {
            intent = new Intent(this, LandingPageActivity.class);
        } else if (itemId == R.id.nav_feed) {
            intent = new Intent(this, FeedActivity.class);
        } else if (itemId == R.id.nav_notifications) {
            // Instead of just returning true, refresh the current activity if needed
            return true;
        } else if (itemId == R.id.nav_more) {
            intent = new Intent(this, MoreActivity.class);
        }

        if (intent != null) {
            startActivityWithAnimation(intent);
            return true;
        }

        return false;
    }

    private void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recyclerView != null) {
            recyclerView.setAdapter(null);
        }
        // Clear any references
        adapter = null;
        items = null;
    }
}