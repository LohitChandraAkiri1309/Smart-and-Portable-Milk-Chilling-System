package com.sih.milkchilling.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sih.milkchilling.R;
import com.sih.milkchilling.data.local.CanEntity;
import com.sih.milkchilling.data.local.CanStatus;

import java.util.ArrayList;
import java.util.List;

public class CanAdapter extends RecyclerView.Adapter<CanAdapter.CanViewHolder> {

    private List<CanEntity> cans = new ArrayList<>();
    private OnCanClickListener listener;

    public interface OnCanClickListener {
        void onCanClick(CanEntity can);
    }

    public CanAdapter(OnCanClickListener listener) {
        this.listener = listener;
    }

    public void setCans(List<CanEntity> cans) {
        this.cans = cans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_can, parent, false);
        return new CanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CanViewHolder holder, int position) {
        CanEntity can = cans.get(position);
        holder.tvName.setText(can.name);
        holder.tvSub.setText("ID: " + can.id + " • Farmer: " + (can.currentFarmerId != null ? can.currentFarmerId : "Unassigned"));
        holder.tvTemp.setText(can.currentTempC + "°C");
        holder.tvBattery.setText("Battery: " + can.batteryLevel + "%");
        holder.tvWifi.setText(can.isConnectedWiFi ? "WiFi Online" : "OFFLINE (" + can.offlineBufferedCount + " buf)");
        holder.tvWifi.setTextColor(can.isConnectedWiFi ? Color.parseColor("#00E5FF") : Color.parseColor("#FFB300"));

        if (can.status == CanStatus.SAFE) {
            holder.tvBadge.setText("SAFE (<4°C)");
            holder.tvBadge.setTextColor(Color.parseColor("#00E676"));
            holder.tvBadge.setBackgroundColor(Color.parseColor("#2000E676"));
        } else if (can.status == CanStatus.WARNING) {
            holder.tvBadge.setText("WARNING");
            holder.tvBadge.setTextColor(Color.parseColor("#FFB300"));
            holder.tvBadge.setBackgroundColor(Color.parseColor("#20FFB300"));
        } else if (can.status == CanStatus.BREACH) {
            holder.tvBadge.setText("BREACH (>8°C)");
            holder.tvBadge.setTextColor(Color.parseColor("#FF5252"));
            holder.tvBadge.setBackgroundColor(Color.parseColor("#20FF5252"));
        } else {
            holder.tvBadge.setText("OFFLINE");
            holder.tvBadge.setTextColor(Color.parseColor("#78909C"));
            holder.tvBadge.setBackgroundColor(Color.parseColor("#2078909C"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCanClick(can);
        });
    }

    @Override
    public int getItemCount() {
        return cans.size();
    }

    static class CanViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSub, tvTemp, tvBattery, tvWifi, tvBadge;

        public CanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_can_name);
            tvSub = itemView.findViewById(R.id.tv_can_sub);
            tvTemp = itemView.findViewById(R.id.tv_can_temp);
            tvBattery = itemView.findViewById(R.id.tv_can_battery);
            tvWifi = itemView.findViewById(R.id.tv_can_wifi);
            tvBadge = itemView.findViewById(R.id.tv_can_badge);
        }
    }
}
