package com.vivo.vivorajonboarding;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

public class LoadingDialog {
    Context context;
    Dialog dialog;

    public LoadingDialog(Context context) {
        this.context = context;
    }

    public void showDialog(String title) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);

        tvTitle.setText(title);

        dialog.setCancelable(false);
        //dialog.setCanceledOnTouchOutside(true);
        dialog.create();
        dialog.show();
    }

    public void hideDialog() {
        dialog.dismiss();
    }
}
