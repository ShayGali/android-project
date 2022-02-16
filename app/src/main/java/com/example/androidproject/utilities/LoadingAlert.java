package com.example.androidproject.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.androidproject.R;

public class LoadingAlert {

    Activity activity;
    AlertDialog dialog;

    public LoadingAlert(Activity activity) {
        this.activity = activity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_alert, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }
     public void dismissDialog(){
        dialog.dismiss();
     }

}
