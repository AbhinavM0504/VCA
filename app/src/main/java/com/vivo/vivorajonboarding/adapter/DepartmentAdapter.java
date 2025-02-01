package com.vivo.vivorajonboarding.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.Department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder> {
    private List<Department> departments = new ArrayList<>();

    @NonNull
    @Override
    public DepartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_department, parent, false);
        return new DepartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentViewHolder holder, int position) {
        holder.bind(departments.get(position));
    }

    @Override
    public int getItemCount() {
        return departments.size();
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
        notifyDataSetChanged();
    }

    class DepartmentViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ImageView iconView;
        private final TextView nameView;
        private final TextView managerView;

        DepartmentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            iconView = itemView.findViewById(R.id.ivDepartmentIcon);
            nameView = itemView.findViewById(R.id.tvDepartmentName);
            managerView = itemView.findViewById(R.id.tvManagerName);
        }

        void bind(final Department department) {
            cardView.setCardBackgroundColor(department.getColor());
            iconView.setImageResource(department.getIcon());
            nameView.setText(department.getName());
            managerView.setText(department.getManager());

            // Add touch animation
            cardView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.95f).scaleY(0.95f)
                                .setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f)
                                .setDuration(100).start();
                        break;
                }
                return false;
            });
        }
    }
}