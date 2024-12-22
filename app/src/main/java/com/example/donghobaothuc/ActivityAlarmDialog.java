package com.example.donghobaothuc;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityAlarmDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alarm_settings);

        // Get the alarm time from the Intent
        Intent intent = getIntent();
        String alarmTime = intent.getStringExtra("alarm_time");

        // Show the dialog
        showAlarmDialog(alarmTime);
    }

    private void showAlarmDialog(String alarmTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alarm Triggered!")
                .setMessage("Alarm for " + alarmTime)
                .setCancelable(false) // Prevent accidental dismissal
                .setPositiveButton("Dismiss", (dialog, which) -> {
                    Toast.makeText(this, "Alarm Dismissed", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish(); // Close the activity
                })
                .setNegativeButton("Snooze", (dialog, which) -> {
                    Toast.makeText(this, "Snoozed", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    // Handle snooze logic here
                    finish(); // Close the activity after snooze
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
