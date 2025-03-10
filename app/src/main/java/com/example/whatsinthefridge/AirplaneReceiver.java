package com.example.whatsinthefridge;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

public class AirplaneReceiver extends BroadcastReceiver {
    AlertDialog dialog;

    public AirplaneReceiver (AlertDialog dialog)
    {
        this.dialog = dialog;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
        Log.d("ALMA", "AIRPLANE MODE STATUS = "+status);
        String msg ="";
        if (status == 0)
            msg = "APP is working when not in airplane mode - go on";
        else
            msg = "ATTENTION !!! APP cannot work when in airplane mode!!!";
        dialog.setMessage(msg);
        dialog.show();
    }
}
