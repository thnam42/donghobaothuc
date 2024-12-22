package com.example.donghobaothuc;

import android.media.RingtoneManager;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Alarm {
    private long id;
    private int hour;
    private int minute;
    private boolean isEnabled;

    private int snoozeTime;

    private boolean[] repeatDays;
    private Uri soundUri;

    private boolean isRepeatWeekly;
    private List<Calendar> specificDates;

    public Alarm(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        this.isEnabled = true;
        this.id = System.currentTimeMillis();
        this.repeatDays = new boolean[7];
        this.soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        this.specificDates = new ArrayList<>();
        this.isRepeatWeekly = false;
    }

    public boolean isRepeatWeekly() { return isRepeatWeekly; }
    public void setRepeatWeekly(boolean repeatWeekly) { isRepeatWeekly = repeatWeekly; }

    public List<Calendar> getSpecificDates() { return specificDates; }
    public void setSpecificDates(List<Calendar> specificDates) { this.specificDates = specificDates; }

    public boolean[] getRepeatDays() { return repeatDays; }
    public void setRepeatDays(boolean[] repeatDays) { this.repeatDays = repeatDays; }

    public Uri getSoundUri() { return soundUri; }
    public void setSoundUri(Uri soundUri) { this.soundUri = soundUri; }

    public boolean isRepeating() {
        for (boolean day : repeatDays) {
            if (day) return true;
        }
        return false;
    }

    public int getSnoozeTime() {
        return snoozeTime;
    }

    public void setSnoozeTime(int snoozeTime) {
        this.snoozeTime = snoozeTime;
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }

    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    public String getTimeString() {
        return String.format("%02d:%02d", hour, minute);
    }

    public String getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1); // Move to the next day if time has passed
        }

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        return dayFormat.format(calendar.getTime());
    }
}