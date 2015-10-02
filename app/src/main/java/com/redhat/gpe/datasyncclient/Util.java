package com.redhat.gpe.datasyncclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Util {

    public static void showMessage(Context pContext, String pTitle, String pMessage){
        AlertDialog.Builder alert = new AlertDialog.Builder(pContext);
        alert.setTitle(pTitle);
        alert.setMessage(pMessage);
        alert.setCancelable(false);
        alert.setPositiveButton("Ok",
                new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.create();
        alert.show();
    }

}
