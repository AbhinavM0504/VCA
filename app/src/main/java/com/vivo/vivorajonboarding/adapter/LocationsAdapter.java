
package com.vivo.vivorajonboarding.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.data.LocationData;

import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationViewHolder> {
    private final List<LocationData> locations;
    private final OnLocationClickListener listener;

    public interface OnLocationClickListener {
        void onLocationClick(int position);
    }

    public LocationsAdapter(List<LocationData> locations, OnLocationClickListener listener) {
        this.locations = locations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationData location = locations.get(position);
        holder.titleText.setText(location.title);
        holder.thumbnail.setImageResource(location.thumbnailResourceId);
        holder.itemView.setOnClickListener(v -> listener.onLocationClick(position));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView titleText;

        LocationViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.locationThumbnail);
            titleText = itemView.findViewById(R.id.locationTitle);
        }
    }
}
