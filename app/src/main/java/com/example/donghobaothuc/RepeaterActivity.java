package com.example.donghobaothuc;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RepeaterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alarm_settings);

        // Retrieve the alarm ID passed from the intent
        int alarmId = getIntent().getIntExtra("ALARM_ID", -1);

        // Use alarmId to fetch or save repeat configurations for this specific alarm
    }
}
