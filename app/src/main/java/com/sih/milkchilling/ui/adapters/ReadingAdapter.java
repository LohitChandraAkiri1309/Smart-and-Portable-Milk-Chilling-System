package com.sih.milkchilling.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sih.milkchilling.R;
import com.sih.milkchilling.data.local.ReadingEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReadingAdapter extends RecyclerView.Adapter<ReadingAdapter.ReadingViewHolder> {

    private List<ReadingEntity> readings = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public void setReadings(List<ReadingEntity> readings) {
        this.readings = readings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReadingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reading, parent, false);
        return new ReadingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadingViewHolder holder, int position) {
        ReadingEntity r = readings.get(position);
        holder.tvTime.setText(dateFormat.format(new Date(r.timestamp)));
        holder.tvTemp.setText(r.temperatureC + "°C");

        if (r.temperatureC <= 4.0) {
            holder.tvTemp.setTextColor(Color.parseColor("#00E676"));
        } else if (r.temperatureC <= 8.0) {
            holder.tvTemp.setTextColor(Color.parseColor("#FFB300"));
        } else {
            holder.tvTemp.setTextColor(Color.parseColor("#FF5252"));
        }

        holder.tvStatus.setText(r.isSynced ? "SYNCED" : "BUFFERED");
        holder.tvStatus.setTextColor(r.isSynced ? Color.parseColor("#00E5FF") : Color.parseColor("#FFB300"));
    }

    @Override
    public int getItemCount() {
        return readings.size();
    }

    static class ReadingViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvTemp, tvStatus;

        public ReadingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_reading_time);
            tvTemp = itemView.findViewById(R.id.tv_reading_temp);
            tvStatus = itemView.findViewById(R.id.tv_reading_status);
        }
    }
}
