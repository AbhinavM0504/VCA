package com.vivo.vivorajonboarding.adapter;


import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.Document;

import java.util.List;
import java.util.function.Consumer;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {
    private List<Document> documents;
    private Consumer<Document> onPickDocument;

    private boolean enabled = true;

    private static final String TAG = "DocumentAdapter";

    public DocumentAdapter(List<Document> documents, Consumer<Document> onPickDocument) {
        this.documents = documents;
        this.onPickDocument = onPickDocument;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document_upload, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Document document = documents.get(position);
        
        // Reset views to default state first
        holder.itemView.clearAnimation();
        holder.documentIcon.setAlpha(0.5f);
        holder.documentTitle.setAlpha(0.5f);
        holder.uploadFilename.setVisibility(View.GONE);
        holder.mainLayout.setBackgroundColor(Color.TRANSPARENT);
        
        holder.documentTitle.setText(document.getTitle());
        holder.documentIcon.setImageResource(document.getIconResource());
        
        if (document.getFileName() != null) {
            holder.uploadFilename.setText(document.getFileName());
            Log.d(TAG, "Document " + document.getTitle() + " has file: " + document.getFileName() 
                + ", Uploaded: " + document.isUploaded());
        } else {
            holder.uploadFilename.setText("No file selected");
            Log.d(TAG, "Document " + document.getTitle() + " has no file");
        }

        updateButtonState(holder, document);
        holder.addButton.setEnabled(enabled);

        // Add animation for items
        setAnimation(holder.itemView, position);
    }

    private void updateButtonState(ViewHolder holder, Document document) {
        Context context = holder.addButton.getContext();
        
        // Reset button to default state
        holder.addButton.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue_doc)));
        holder.addButton.setTextColor(ContextCompat.getColor(context, R.color.blue_doc));
        holder.addButton.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue_doc)));

        if (document.isUploaded()) {
            Log.d(TAG, "Document is uploaded: " + document.getTitle());
            holder.addButton.setText("Change");
            holder.addButton.setIconResource(R.drawable.ic_check);
            holder.uploadFilename.setVisibility(View.VISIBLE);
            holder.documentIcon.setAlpha(1.0f);
            holder.documentTitle.setAlpha(1.0f);
            holder.addButton.setIconTint(null);
            holder.addButton.setTextColor(ContextCompat.getColor(context, R.color.green_check));
            holder.addButton.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_check)));
            holder.mainLayout.setBackgroundColor(Color.parseColor("#8CE8F5E9")); // Material Design 50 green
        } else {
            Log.d(TAG, "Document not uploaded: " + document.getTitle());
            holder.addButton.setText("Add");
            holder.addButton.setIconResource(R.drawable.ic_add);
        }

        // Set click listener only once
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.addButton.setOnClickListener(v -> {
                Log.d(TAG, "Clicked add/change button for: " + document.getTitle());
                onPickDocument.accept(document);
            });
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public void updateDocument(Document document) {
        int position = documents.indexOf(document);
        if (position != -1) {
            documents.set(position, document);
            Log.d(TAG, "Updating document at position " + position + ": " + document.getTitle() 
                + ", Uploaded: " + document.isUploaded() 
                + ", Filename: " + document.getFileName());
            notifyItemChanged(position);
        } else {
            Log.e(TAG, "Document not found in list: " + document.getTitle());
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        Animation animation = AnimationUtils.loadAnimation(
                viewToAnimate.getContext(),
                R.anim.item_animation_fall_down
        );
        animation.setStartOffset(position * 10L);
        viewToAnimate.startAnimation(animation);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView documentIcon;
        TextView documentTitle;
        TextView uploadFilename;
        MaterialButton addButton;
        LinearLayout mainLayout;

        ViewHolder(View view) {
            super(view);
            mainLayout=view.findViewById(R.id.mainLayout);
            documentIcon = view.findViewById(R.id.documentIcon);
            documentTitle = view.findViewById(R.id.documentTitle);
            uploadFilename = view.findViewById(R.id.uploadFilename);
            addButton = view.findViewById(R.id.addButton);
        }
    }
}
