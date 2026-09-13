package com.sih.milkchilling.ui.detail;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sih.milkchilling.MilkChillingApp;
import com.sih.milkchilling.R;
import com.sih.milkchilling.data.local.ReadingEntity;
import com.sih.milkchilling.data.repository.MilkChillingRepository;
import com.sih.milkchilling.ui.adapters.ReadingAdapter;
import com.sih.milkchilling.ui.components.ChillingCurveView;

import java.util.Collections;
import java.util.List;

public class CanDetailActivity extends AppCompatActivity {

    private TextView btnBack, tvTitle, tvTimeToTarget;
    private ChillingCurveView chillingChart;
    private RecyclerView rvReadings;
    private ReadingAdapter adapter;
    private MilkChillingRepository repository;
    private String canId = "CAN-ESP32-01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_can_detail);

        if (getIntent() != null && getIntent().hasExtra("CAN_ID")) {
            canId = getIntent().getStringExtra("CAN_ID");
        }

        btnBack = findViewById(R.id.btn_back);
        tvTitle = findViewById(R.id.tv_detail_title);
        tvTimeToTarget = findViewById(R.id.tv_time_to_target);
        chillingChart = findViewById(R.id.chilling_chart);
        rvReadings = findViewById(R.id.rv_readings);

        tvTitle.setText("Can Details: " + canId);
        btnBack.setOnClickListener(v -> finish());

        rvReadings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReadingAdapter();
        rvReadings.setAdapter(adapter);

        repository = MilkChillingApp.getInstance().getRepository();
        repository.getReadingsForCanLiveData(canId).observe(this, this::updateReadings);
    }

    private void updateReadings(List<ReadingEntity> readings) {
        if (readings == null || readings.isEmpty()) return;
        adapter.setReadings(readings);
        chillingChart.setReadings(readings);

        // Calculate Time to Target (35°C -> 4°C)
        ReadingEntity start = readings.get(readings.size() - 1); // oldest
        ReadingEntity target = null;
        for (int i = readings.size() - 1; i >= 0; i--) {
            if (readings.get(i).temperatureC <= 4.0) {
                target = readings.get(i);
                break;
            }
        }

        if (target != null) {
            int mins = (int) ((target.timestamp - start.timestamp) / (60 * 1000));
            tvTimeToTarget.setText(mins + " Mins");
        } else {
            tvTimeToTarget.setText("Chilling in progress...");
        }
    }
}
