package com.example.donghobaothuc;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmSnoozeReceiver extends BroadcastReceiver {
    private static final int SNOOZE_MINUTES = 5;

    @Override
    public void onReceive(Context context, Intent intent) {
        long alarmId = intent.getLongExtra("ALARM_ID", 0);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.MINUTE, SNOOZE_MINUTES);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("ALARM_ID", alarmId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) alarmId,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }

        // Cancel the current notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel((int) alarmId);

        Toast.makeText(context, "Alarm snoozed for " + SNOOZE_MINUTES + " minutes", Toast.LENGTH_SHORT).show();
    }
}