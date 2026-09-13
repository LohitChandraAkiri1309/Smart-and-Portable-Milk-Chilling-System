package com.sih.milkchilling.ui.alerts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sih.milkchilling.MilkChillingApp;
import com.sih.milkchilling.R;
import com.sih.milkchilling.data.local.ReadingEntity;
import com.sih.milkchilling.data.repository.MilkChillingRepository;
import com.sih.milkchilling.ui.adapters.AlertAdapter;
import com.sih.milkchilling.util.ReportExporter;

import java.io.File;
import java.util.List;

public class AlertsFragment extends Fragment {

    private RecyclerView rvAlerts;
    private AlertAdapter adapter;
    private Button btnExportCsv;
    private MilkChillingRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);

        rvAlerts = view.findViewById(R.id.rv_alerts);
        rvAlerts.setLayoutManager(new LinearLayoutManager(getContext()));

        repository = MilkChillingApp.getInstance().getRepository();

        adapter = new AlertAdapter(alertId -> repository.resolveAlert(alertId));
        rvAlerts.setAdapter(adapter);

        btnExportCsv = view.findViewById(R.id.btn_export_csv);
        btnExportCsv.setOnClickListener(v -> exportCsv());

        repository.getAllAlertsLiveData().observe(getViewLifecycleOwner(), alerts -> {
            if (alerts != null) adapter.setAlerts(alerts);
        });

        return view;
    }

    private void exportCsv() {
        repository.getReadingsForBatchLiveData("BATCH-20260901-01").observe(getViewLifecycleOwner(), readings -> {
            if (readings != null && !readings.isEmpty()) {
                File file = ReportExporter.exportBatchToCsv(requireContext(), "BATCH-20260901-01", readings);
                String msg = (file != null) ? "CSV Exported to: " + file.getAbsolutePath() : "Failed to export CSV";
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
