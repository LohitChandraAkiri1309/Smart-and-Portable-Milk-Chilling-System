package com.sih.milkchilling.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sih.milkchilling.R;
import com.sih.milkchilling.data.local.AlertEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {

    private List<AlertEntity> alerts = new ArrayList<>();
    private final OnAlertResolveListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());

    public interface OnAlertResolveListener {
        void onResolve(long alertId);
    }

    public AlertAdapter(OnAlertResolveListener listener) {
        this.listener = listener;
    }

    public void setAlerts(List<AlertEntity> alerts) {
        this.alerts = alerts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        AlertEntity a = alerts.get(position);
        holder.tvTitle.setText(a.type.name() + " • " + a.canId);
        holder.tvTime.setText(dateFormat.format(new Date(a.triggeredAt)));
        holder.tvMsg.setText(a.message);

        if (a.isResolved) {
            holder.btnResolve.setText("RESOLVED");
            holder.btnResolve.setEnabled(false);
        } else {
            holder.btnResolve.setText("Resolve");
            holder.btnResolve.setEnabled(true);
            holder.btnResolve.setOnClickListener(v -> {
                if (listener != null) listener.onResolve(a.id);
            });
        }
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    static class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvMsg;
        Button btnResolve;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_alert_title);
            tvTime = itemView.findViewById(R.id.tv_alert_time);
            tvMsg = itemView.findViewById(R.id.tv_alert_msg);
            btnResolve = itemView.findViewById(R.id.btn_resolve_alert);
        }
    }
}
