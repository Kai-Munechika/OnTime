package com.kaim808.transitalarm.receivers_and_services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CancelMyAlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Cancel receiver", Toast.LENGTH_SHORT).show();
        context.stopService(new Intent(context, MyService.class));
    }
}
