package com.example.donghobaothuc;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AlarmAdapter.OnAlarmToggleListener,
        AlarmAdapter.OnSettingsClickListener {

    private RecyclerView alarmRecyclerView;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarms;
    private ImageButton btnSetAlarm;
    private ImageButton btnCancelAlarm;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static final int PICK_AUDIO_REQUEST = 1;
    private Alarm currentEditingAlarm;

    private DateAdapter dateAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmRecyclerView = findViewById(R.id.alarmRecyclerView);
        alarmAdapter = new AlarmAdapter(this,this, this);
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alarmRecyclerView.setAdapter(alarmAdapter);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarms = new ArrayList<>();

        btnSetAlarm = findViewById(R.id.btnSetAlarm);
        btnCancelAlarm = findViewById(R.id.btnCancelAlarm);

        btnSetAlarm.setOnClickListener(v -> checkPermissionsAndShowTimePicker());
        btnCancelAlarm.setOnClickListener(v -> cancelAllAlarms());
    }

    private void setAlarm(int hourOfDay, int minute) {
        Alarm alarm = new Alarm(hourOfDay, minute);
        alarms.add(alarm);
        alarmAdapter.setAlarms(alarms);
        scheduleAlarm(alarm);
    }

    private void scheduleAlarm(Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("ALARM_ID", alarm.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) alarm.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );

        Toast.makeText(this, "Alarm set for " + alarm.getTimeString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAlarmToggle(Alarm alarm, boolean isEnabled) {
        if (isEnabled) {
            scheduleAlarm(alarm);
        } else {
            cancelAlarm(alarm);
        }
    }

    @Override
    public void onSettingsClick(Alarm alarm) {
        showAlarmSettingsDialog(alarm);  // This will show your settings dialog
    }

    private void checkPermissionsAndShowTimePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        showTimePickerDialog();
    }

    private void showTimePickerDialog() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> setAlarm(hourOfDay, minute),
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true).show();
    }

    private void cancelAlarm(Alarm alarm) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) alarm.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm cancelled", Toast.LENGTH_SHORT).show();
    }

    private void cancelAllAlarms() {
        for (Alarm alarm : alarms) {
            cancelAlarm(alarm);
        }
        alarms.clear();
        alarmAdapter.setAlarms(alarms);
    }

    private void showAlarmSettingsDialog(final Alarm alarm) {
        currentEditingAlarm = alarm;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_alarm_settings, null);

        RadioGroup repeatTypeGroup = dialogView.findViewById(R.id.repeatTypeGroup);
        LinearLayout weeklyRepeatLayout = dialogView.findViewById(R.id.weeklyRepeatLayout);
        LinearLayout specificDatesLayout = dialogView.findViewById(R.id.specificDatesLayout);

        // Set up weekly repeat checkboxes
        CheckBox[] dayCheckboxes = new CheckBox[7];
        int[] checkboxIds = {R.id.sunday, R.id.monday, R.id.tuesday, R.id.wednesday,
                R.id.thursday, R.id.friday, R.id.saturday};

        for (int i = 0; i < 7; i++) {
            dayCheckboxes[i] = dialogView.findViewById(checkboxIds[i]);
            dayCheckboxes[i].setChecked(alarm.getRepeatDays()[i]);
        }

        // Set up specific dates RecyclerView
        RecyclerView selectedDatesRecyclerView = dialogView.findViewById(R.id.selectedDatesRecyclerView);
        dateAdapter = new DateAdapter(position -> {
            alarm.getSpecificDates().remove(position);
            dateAdapter.setDates(alarm.getSpecificDates());
        });
        selectedDatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectedDatesRecyclerView.setAdapter(dateAdapter);
        dateAdapter.setDates(alarm.getSpecificDates());

        // Add date button
        Button addDateButton = dialogView.findViewById(R.id.addDateButton);
        addDateButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        alarm.getSpecificDates().add(calendar);
                        dateAdapter.setDates(alarm.getSpecificDates());
                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Radio button listeners
        repeatTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            weeklyRepeatLayout.setVisibility(View.GONE);
            specificDatesLayout.setVisibility(View.GONE);

            if (checkedId == R.id.weeklyRepeatRadio) {
                weeklyRepeatLayout.setVisibility(View.VISIBLE);
                alarm.setRepeatWeekly(true);
            } else if (checkedId == R.id.specificDatesRadio) {
                specificDatesLayout.setVisibility(View.VISIBLE);
                alarm.setRepeatWeekly(false);
            } else {
                alarm.setRepeatWeekly(false);
                alarm.getSpecificDates().clear();
                for (int i = 0; i < 7; i++) {
                    alarm.getRepeatDays()[i] = false;
                }
            }
        });

        // Set initial radio button state
        if (alarm.isRepeatWeekly()) {
            repeatTypeGroup.check(R.id.weeklyRepeatRadio);
            weeklyRepeatLayout.setVisibility(View.VISIBLE);
        } else if (!alarm.getSpecificDates().isEmpty()) {
            repeatTypeGroup.check(R.id.specificDatesRadio);
            specificDatesLayout.setVisibility(View.VISIBLE);
        } else {
            repeatTypeGroup.check(R.id.noRepeatRadio);
        }

        builder.setView(dialogView)
                .setTitle("Cài đặt báo thức")
                .setPositiveButton("Lưu", (dialog, which) -> {
                    if (alarm.isRepeatWeekly()) {
                        boolean[] repeatDays = new boolean[7];
                        for (int i = 0; i < 7; i++) {
                            repeatDays[i] = dayCheckboxes[i].isChecked();
                        }
                        alarm.setRepeatDays(repeatDays);
                    }

                    alarmAdapter.notifyDataSetChanged();
                    if (alarm.isEnabled()) {
                        scheduleAlarm(alarm);
                    }
                })
                .setNegativeButton("Bỏ qua", null)
                .setOnDismissListener(dialog -> currentEditingAlarm = null)
                .show();
    }
}