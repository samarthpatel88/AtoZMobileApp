package com.atozkids.utils.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.ColorDrawable;

import com.atozkids.R;


public class MyProgressDialog extends AlertDialog implements OnDismissListener {
    public MyProgressDialog(Context context) {
        super(context);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    public void show() {
        super.show();
        setContentView(R.layout.dialog_progress);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.dismiss();
    }

}