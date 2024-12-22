package com.example.donghobaothuc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
    private List<Calendar> dates;
    private OnDateDeleteListener listener;

    public interface OnDateDeleteListener {
        void onDateDelete(int position);
    }

    public DateAdapter(OnDateDeleteListener listener) {
        this.dates = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.date_item, parent, false);  // Changed to use item_date layout
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        Calendar date = dates.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.dateText.setText(sdf.format(date.getTime()));

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDateDelete(holder.getAdapterPosition());  // Using getAdapterPosition() instead of position
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public void setDates(List<Calendar> dates) {
        this.dates = new ArrayList<>(dates);
        notifyDataSetChanged();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        ImageButton deleteButton;

        DateViewHolder(View view) {
            super(view);
            dateText = view.findViewById(R.id.dateText);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }
}