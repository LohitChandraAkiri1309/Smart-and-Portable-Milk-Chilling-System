package com.sih.milkchilling.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sih.milkchilling.R;
import com.sih.milkchilling.data.local.FarmerEntity;

import java.util.ArrayList;
import java.util.List;

public class FarmerAdapter extends RecyclerView.Adapter<FarmerAdapter.FarmerViewHolder> {

    private List<FarmerEntity> farmers = new ArrayList<>();

    public void setFarmers(List<FarmerEntity> farmers) {
        this.farmers = farmers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FarmerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farmer, parent, false);
        return new FarmerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmerViewHolder holder, int position) {
        FarmerEntity f = farmers.get(position);
        holder.tvName.setText(f.name);
        holder.tvDetails.setText("Phone: " + f.phone + " • " + f.village);
        holder.tvCan.setText("Can: " + (f.activeCanId != null ? f.activeCanId : "None"));
    }

    @Override
    public int getItemCount() {
        return farmers.size();
    }

    static class FarmerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvCan;

        public FarmerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_farmer_name);
            tvDetails = itemView.findViewById(R.id.tv_farmer_details);
            tvCan = itemView.findViewById(R.id.tv_farmer_can);
        }
    }
}
