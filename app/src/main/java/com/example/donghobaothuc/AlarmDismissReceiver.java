package com.example.donghobaothuc;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmDismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long alarmId = intent.getLongExtra("ALARM_ID", 0);

        // Cancel the notification associated with the alarm
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel((int) alarmId);

        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.stopAlarm(context);

        Toast.makeText(context, "Alarm dismissed", Toast.LENGTH_SHORT).show();
    }
}
