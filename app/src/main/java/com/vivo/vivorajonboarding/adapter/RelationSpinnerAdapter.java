package com.vivo.vivorajonboarding.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.RelationItem;
import java.util.List;

public class RelationSpinnerAdapter extends ArrayAdapter<RelationItem> {
    private final LayoutInflater inflater;
    private final List<RelationItem> relations;
    private final Context context;

    public RelationSpinnerAdapter(@NonNull Context context, @NonNull List<RelationItem> relations) {
        super(context, R.layout.custom_spinner_item, relations);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.relations = relations;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = inflater.inflate(R.layout.custom_spinner_item, parent, false);
            holder = new ViewHolder();
            holder.icon = view.findViewById(R.id.icon);
            holder.name = view.findViewById(R.id.text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        RelationItem relationItem = relations.get(position);

        if (relationItem != null) {
            holder.icon.setImageResource(relationItem.getIconRes());
            holder.name.setText(relationItem.getName());
        }

        return view;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView name;
    }

    public int getPositionByName(String relationName) {
        for (int i = 0; i < relations.size(); i++) {
            if (relations.get(i).getName().equals(relationName)) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    @Override
    public RelationItem getItem(int position) {
        return relations.get(position);
    }

    @Override
    public int getCount() {
        return relations.size();
    }
}