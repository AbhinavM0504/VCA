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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.model.InsuranceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InsuranceAdapter extends RecyclerView.Adapter<InsuranceAdapter.ViewHolder> {


    Context context;
    ArrayList<InsuranceModel> insuranceMembersList;

    public InsuranceAdapter(Context context, ArrayList<InsuranceModel> insuranceMembersList) {
        this.context = context;
        this.insuranceMembersList = insuranceMembersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.insurance_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InsuranceModel model = insuranceMembersList.get(position);
        //set value to fields
        holder.memberRelationTv.setText(model.getMemberRelation());
        holder.memberNameTv.setText(model.getMemberName());
        holder.memberDobTv.setText(model.getMemberDob());

        holder.acFrontTv.setText(model.getMemberAcFrontImage());
        holder.acFrontTv.setText(Html.fromHtml(String.format("<u>%s</u>", "Click here")));
        holder.acFrontTv.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getMemberAcFrontImage()));
            context.startActivity(browserIntent);
        });

        holder.acBackTv.setText(model.getMemberAcBackImage());
        holder.acBackTv.setText(Html.fromHtml(String.format("<u>%s</u>", "Click here")));
        holder.acBackTv.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getMemberAcBackImage()));
            context.startActivity(browserIntent);
        });

        //handle moreButton visibility
        if (!model.getRequest().isEmpty()) {
            holder.moreButton.setVisibility(View.GONE);
            holder.moreButton.setEnabled(false);
        } else {
            holder.moreButton.setVisibility(View.VISIBLE);
            holder.moreButton.setEnabled(true);
        }

        holder.moreButton.setOnClickListener(view -> {
            PopupMenu imageMenu = new PopupMenu(context, holder.moreButton);
            imageMenu.getMenu().add(Menu.NONE, 0, 0, "Remove");
            imageMenu.show();

            imageMenu.setOnMenuItemClickListener(imageMenuItem -> {
                int i = imageMenuItem.getItemId();
                if (i == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure to remove this member ?");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        dialog.cancel();
                        removeInsuranceMemberById(model.getId(), position);
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

    private void removeInsuranceMemberById(String id, int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.REMOVE_INSURANCE_DATA_BY_ID_URL, response -> {
            Toast.makeText(context, "" + response.trim(), Toast.LENGTH_SHORT).show();

            insuranceMembersList.remove(position);
            notifyItemRemoved(position);

        }, error -> {
            Log.e("INSURANCE ADAPTER", error.toString());
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
        return insuranceMembersList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView memberRelationTv, memberNameTv, memberDobTv, acFrontTv, acBackTv;
        ImageButton moreButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberRelationTv = itemView.findViewById(R.id.memberRelationTv);
            memberNameTv = itemView.findViewById(R.id.memberNameTv);
            memberDobTv = itemView.findViewById(R.id.memberDobTv);
            acFrontTv = itemView.findViewById(R.id.acFrontTv);
            acBackTv = itemView.findViewById(R.id.acBackTv);
            moreButton = itemView.findViewById(R.id.moreButton);
        }
    }
}
