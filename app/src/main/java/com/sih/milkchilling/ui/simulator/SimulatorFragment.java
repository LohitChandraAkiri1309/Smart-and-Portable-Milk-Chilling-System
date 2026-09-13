package com.sih.milkchilling.ui.simulator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sih.milkchilling.R;
import com.sih.milkchilling.data.simulator.CoolingProfile;
import com.sih.milkchilling.data.simulator.ESP32HardwareSimulator;
import com.sih.milkchilling.data.simulator.SimulatorState;

public class SimulatorFragment extends Fragment {

    private TextView tvWiFiStatus, tvTemp, tvMins, tvBuffered, tvLog;
    private Button btnStart, btnToggleWiFi, btnBreach, btnStop;
    private ESP32HardwareSimulator simulator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simulator, container, false);

        tvWiFiStatus = view.findViewById(R.id.tv_sim_wifi_status);
        tvTemp = view.findViewById(R.id.tv_sim_temp);
        tvMins = view.findViewById(R.id.tv_sim_mins);
        tvBuffered = view.findViewById(R.id.tv_sim_buffered);
        tvLog = view.findViewById(R.id.tv_sim_log);

        btnStart = view.findViewById(R.id.btn_start_sim);
        btnToggleWiFi = view.findViewById(R.id.btn_toggle_wifi);
        btnBreach = view.findViewById(R.id.btn_inject_breach);
        btnStop = view.findViewById(R.id.btn_stop_sim);

        simulator = ESP32HardwareSimulator.getInstance();
        simulator.getStateLiveData().observe(getViewLifecycleOwner(), this::updateState);

        btnStart.setOnClickListener(v -> simulator.startChillingCycle("CAN-ESP32-01", CoolingProfile.NORMAL_PCM_SOLAR));
        btnToggleWiFi.setOnClickListener(v -> simulator.toggleWiFiConnection());
        btnBreach.setOnClickListener(v -> simulator.injectBreachScenario());
        btnStop.setOnClickListener(v -> simulator.stopSimulation());

        return view;
    }

    private void updateState(SimulatorState state) {
        if (state == null) return;
        tvWiFiStatus.setText(state.isWiFiConnected ? "ONLINE" : "OFFLINE (FLASH BUFFER)");
        tvWiFiStatus.setTextColor(state.isWiFiConnected ? 0xFF00E5FF : 0xFFFF5252);
        tvTemp.setText(state.currentTempC + "°C");
        tvMins.setText(state.elapsedMinutes + " Mins");
        tvBuffered.setText(state.bufferedCount + " Records");
        tvLog.setText(state.statusText);

        btnToggleWiFi.setText(state.isWiFiConnected ? "Simulate WiFi Disconnect (Buffer in ESP32 Flash)" : "Reconnect WiFi (Perform Burst Sync Upload)");
    }
}
