package com.kaim808.transitalarm.receivers_and_services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE_SUNDAY    = 777770;
    public static final int REQUEST_CODE_MONDAY    = 777771;
    public static final int REQUEST_CODE_TUESDAY   = 777772;
    public static final int REQUEST_CODE_WEDNESDAY = 777773;
    public static final int REQUEST_CODE_THURSDAY  = 777774;
    public static final int REQUEST_CODE_FRIDAY    = 777775;
    public static final int REQUEST_CODE_SATURDAY  = 777776;

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "BroadCastReceiver Call...", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(context, MyService.class);
        i.putExtras(intent.getExtras());
        context.startService(i);
    }
}
