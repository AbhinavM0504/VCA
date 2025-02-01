package com.vivo.vivorajonboarding.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.GenderItem;

import java.util.List;

public class GenderSpinnerAdapter extends ArrayAdapter<GenderItem> {
    private final Context context;
    private final List<GenderItem> items;

    public GenderSpinnerAdapter(Context context, List<GenderItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }

    private View createCustomView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_item, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.icon);
        TextView text = convertView.findViewById(R.id.text);

        GenderItem item = getItem(position);
        if (item != null) {
            if (item.getIcon() != null) {
                icon.setImageDrawable(item.getIcon());
            }
            text.setText(item.getText());
        }

        return convertView;
    }
}