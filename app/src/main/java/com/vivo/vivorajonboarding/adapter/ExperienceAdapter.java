package com.vivo.vivorajonboarding.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.model.ExperienceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ViewHolder> {


    Context context;
    ArrayList<ExperienceModel> experienceList;

    public ExperienceAdapter(Context context, ArrayList<ExperienceModel> experienceList) {
        this.context = context;
        this.experienceList = experienceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.experience_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExperienceModel experienceModel = experienceList.get(position);
        Log.d("REQUEST STATUS", experienceModel.getRequest());
        //set value to fields
        holder.companyNameTv.setText(experienceModel.getCompany_name());
        holder.jobTitleTv.setText(experienceModel.getCompany_job_title());
        holder.startDateTv.setText(experienceModel.getCompany_start_date());
        holder.endDateTv.setText(experienceModel.getCompany_end_date());
        if (experienceModel.getExperience_letter() != null && !experienceModel.getExperience_letter().equalsIgnoreCase("")) {

            Log.e("EXPERIENCE LETTER", "PRESENT");
            holder.experienceLetterLayout.setVisibility(View.VISIBLE);

            holder.expLetterTv.setText(Html.fromHtml(String.format("<u>%s</u>", "Click here")));
            holder.expLetterTv.setOnClickListener(view -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(experienceModel.getExperience_letter()));
                context.startActivity(browserIntent);
            });
        } else {
            Log.e("EXPERIENCE LETTER", "ABSENT");
            holder.experienceLetterLayout.setVisibility(View.GONE);
        }
        if (!experienceModel.getRequest().isEmpty()) {
            holder.moreButton.setVisibility(View.INVISIBLE);
            holder.moreButton.setEnabled(false);
        } else {
            holder.moreButton.setVisibility(View.VISIBLE);
            holder.moreButton.setEnabled(true);
        }
        holder.moreButton.setOnClickListener(view -> {
            PopupMenu imageMenu = new PopupMenu(context, holder.moreButton);
            imageMenu.getMenu().add(Menu.NONE, 0, 0, "Remove");

            imageMenu.show();
            //handle menu item clicks
            imageMenu.setOnMenuItemClickListener(imageMenuItem -> {
                int i = imageMenuItem.getItemId();
                if (i == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure to remove this experience ?");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        dialog.cancel();
                        //remove experience data by id
                        removeExperienceData(experienceModel.getId(), position);
                    });
                    builder.setNegativeButton("No", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.show();
                }
                return false;
            });
        });
    }

    private void removeExperienceData(String id, int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.REMOVE_EXPERIENCE_DATA_BY_ID_URL, response -> {
            Toast.makeText(context, "" + response.trim(), Toast.LENGTH_SHORT).show();

            experienceList.remove(position);
            notifyItemRemoved(position);

        }, error -> {
            Log.e("EditActivity", error.toString());
            Toast.makeText(context, "Error : " + error, Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
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

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return experienceList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView companyNameTv, jobTitleTv, expLetterTv;
        Chip startDateTv,endDateTv;
        ImageButton moreButton;
        CardView experienceLetterLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyNameTv = itemView.findViewById(R.id.companyNameTv);
            jobTitleTv = itemView.findViewById(R.id.jobTitleTv);
            startDateTv = itemView.findViewById(R.id.startDateChip);
            endDateTv = itemView.findViewById(R.id.endDateChip);
            experienceLetterLayout = itemView.findViewById(R.id.experienceLetterCard);
            expLetterTv = itemView.findViewById(R.id.expLetterTv);
            moreButton = itemView.findViewById(R.id.moreButton);
        }
    }
}
