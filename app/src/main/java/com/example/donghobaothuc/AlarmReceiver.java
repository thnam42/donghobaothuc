package com.example.donghobaothuc;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "ALARM_CHANNEL";
    private Ringtone ringtone;

    @Override
    public void onReceive(Context context, Intent intent) {
        long alarmId = intent.getLongExtra("ALARM_ID", -1);

        // Play alarm sound
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        ringtone = RingtoneManager.getRingtone(context, alarmSound);
//        ringtone.play();
//
//        SharedPreferences preferences = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean("RINGTONE_PLAYING", true);
//        editor.apply();

        // Build and display notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Alarm Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent dismissIntent = new Intent(context, AlarmDismissReceiver.class);
        dismissIntent.putExtra("ALARM_ID", alarmId);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, (int) alarmId, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent snoozeIntent = new Intent(context, AlarmSnoozeReceiver.class);
        snoozeIntent.putExtra("ALARM_ID", alarmId);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, (int) alarmId, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Báo thức")
                .setContentText("Đã đến giờ báo thức")
                .setSmallIcon(R.drawable.ic_alarm)
                .addAction(R.drawable.ic_snooze, "Báo lại", snoozePendingIntent)
                .addAction(R.drawable.ic_dismiss, "Bỏ qua", dismissPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify((int) alarmId, notificationBuilder.build());
    }

    public void stopAlarm(Context context) {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }

//        SharedPreferences preferences = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean("RINGTONE_PLAYING", false);
//        editor.apply();
    }
}