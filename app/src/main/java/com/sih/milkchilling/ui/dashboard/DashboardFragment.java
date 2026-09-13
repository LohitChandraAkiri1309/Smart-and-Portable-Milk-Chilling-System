package com.sih.milkchilling.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sih.milkchilling.MilkChillingApp;
import com.sih.milkchilling.R;
import com.sih.milkchilling.data.local.CanEntity;
import com.sih.milkchilling.data.local.CanStatus;
import com.sih.milkchilling.data.repository.MilkChillingRepository;
import com.sih.milkchilling.ui.adapters.CanAdapter;
import com.sih.milkchilling.ui.detail.CanDetailActivity;

import java.util.List;

public class DashboardFragment extends Fragment {

    private TextView tvSafe, tvWarning, tvBreach, tvOffline;
    private RecyclerView rvCans;
    private CanAdapter adapter;
    private MilkChillingRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvSafe = view.findViewById(R.id.tv_safe_count);
        tvWarning = view.findViewById(R.id.tv_warning_count);
        tvBreach = view.findViewById(R.id.tv_breach_count);
        tvOffline = view.findViewById(R.id.tv_offline_count);

        rvCans = view.findViewById(R.id.rv_cans);
        rvCans.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CanAdapter(can -> {
            Intent intent = new Intent(getContext(), CanDetailActivity.class);
            intent.putExtra("CAN_ID", can.id);
            startActivity(intent);
        });
        rvCans.setAdapter(adapter);

        repository = MilkChillingApp.getInstance().getRepository();
        repository.getAllCansLiveData().observe(getViewLifecycleOwner(), this::updateUI);

        return view;
    }

    private void updateUI(List<CanEntity> cans) {
        if (cans == null) return;
        int safe = 0, warning = 0, breach = 0, offline = 0;
        for (CanEntity can : cans) {
            if (can.status == CanStatus.SAFE) safe++;
            else if (can.status == CanStatus.WARNING) warning++;
            else if (can.status == CanStatus.BREACH) breach++;
            if (!can.isConnectedWiFi || can.status == CanStatus.STALE) offline++;
        }

        tvSafe.setText(String.valueOf(safe));
        tvWarning.setText(String.valueOf(warning));
        tvBreach.setText(String.valueOf(breach));
        tvOffline.setText(String.valueOf(offline));

        adapter.setCans(cans);
    }
}
