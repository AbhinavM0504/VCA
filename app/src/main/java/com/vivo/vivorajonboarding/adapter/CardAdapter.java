package com.vivo.vivorajonboarding.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.CardModel;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final List<CardModel> cardList;

    public CardAdapter(List<CardModel> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardModel card = cardList.get(position);
        holder.title.setText(card.getTitle());
        holder.formContainer.removeAllViews();

        // Dynamically add EditText for each form field
        for (String field : card.getFormFields()) {
            View fieldView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.item_form_field, holder.formContainer, false);

            TextView fieldLabel = fieldView.findViewById(R.id.fieldLabel);
            EditText fieldInput = fieldView.findViewById(R.id.fieldInput);

            fieldLabel.setText(field);
            holder.formContainer.addView(fieldView);
        }
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        LinearLayout formContainer;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            formContainer = itemView.findViewById(R.id.formContainer);
        }
    }
}
