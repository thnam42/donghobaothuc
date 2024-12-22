package com.example.donghobaothuc;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    public interface OnSettingsClickListener {
        void onSettingsClick(Alarm alarm);
    }
    private List<Alarm> alarms;
    private OnAlarmToggleListener listener;

    private Context context;
    private OnSettingsClickListener settingsListener;

    public interface OnAlarmToggleListener {
        void onAlarmToggle(Alarm alarm, boolean isEnabled);
    }

    public AlarmAdapter(Context context, OnAlarmToggleListener listener, OnSettingsClickListener settingsListener) {
        this.alarms = new ArrayList<>();
        this.listener = listener;
        this.settingsListener = settingsListener;
        this.context = context;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarms.get(position);
        holder.dayTextView.setText(alarm.getDayOfWeek());
        holder.timeText.setText(alarm.getTimeString());
        holder.alarmSwitch.setChecked(alarm.isEnabled());


        holder.alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarm.setEnabled(isChecked);
            listener.onAlarmToggle(alarm, isChecked);
        });

        holder.btnRepeat.setOnClickListener(v -> {
            // Open Dialog_Alarm_Setting activity or dialog
            if (settingsListener != null) {
                settingsListener.onSettingsClick(alarm);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    public void addAlarm(Alarm alarm) {
        this.alarms.add(alarm);
        notifyItemInserted(alarms.size() - 1);
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        TextView dayTextView;
        Button btnRepeat;
        Switch alarmSwitch;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.timeText);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            btnRepeat = itemView.findViewById(R.id.btnRepeat);
            alarmSwitch = itemView.findViewById(R.id.alarmSwitch);
        }
    }
}