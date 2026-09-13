package com.sih.milkchilling.data.simulator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sih.milkchilling.data.local.CanStatus;
import com.sih.milkchilling.data.local.ReadingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ESP32HardwareSimulator {

    private static ESP32HardwareSimulator INSTANCE;

    private final MutableLiveData<SimulatorState> stateLiveData = new MutableLiveData<>(new SimulatorState());
    private final List<ReadingEntity> offlineFlashBuffer = new ArrayList<>();

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> simulationTask;

    private String activeBatchId = "BATCH-SIM-01";
    private int elapsedMins = 0;
    private double currentTemp = 35.5;

    public interface OnReadingsIngestedListener {
        void onIngest(List<ReadingEntity> readings, String batchId, CanStatus status);
    }

    private OnReadingsIngestedListener listener;

    public static synchronized ESP32HardwareSimulator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = ESP32HardwareSimulator.thisInstance();
        }
        return INSTANCE;
    }

    private static ESP32HardwareSimulator thisInstance() {
        return new ESP32HardwareSimulator();
    }

    public LiveData<SimulatorState> getStateLiveData() {
        return stateLiveData;
    }

    public void setOnReadingsIngestedListener(OnReadingsIngestedListener listener) {
        this.listener = listener;
    }

    public void startChillingCycle(String canId, CoolingProfile profile) {
        stopSimulation();
        activeBatchId = "BATCH-" + canId + "-" + (System.currentTimeMillis() / 1000);
        offlineFlashBuffer.clear();
        elapsedMins = 0;
        currentTemp = 35.5;

        SimulatorState newState = new SimulatorState(
            canId, true, true, 35.5, 98, 0, 0, profile,
            "Chilling cycle initialized for " + canId + " at 35.5°C"
        );
        stateLiveData.postValue(newState);

        if (scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        simulationTask = scheduler.scheduleAtFixedRate(() -> {
            elapsedMins += 5;

            if (profile == CoolingProfile.NORMAL_PCM_SOLAR) {
                currentTemp = 35.5 * Math.exp(-0.035 * elapsedMins) + 3.8;
            } else if (profile == CoolingProfile.SLOW_BREACH) {
                currentTemp = 35.5 * Math.exp(-0.015 * elapsedMins) + 9.5;
            } else if (profile == CoolingProfile.SENSOR_FAULT) {
                currentTemp = (elapsedMins > 15) ? -50.0 : 35.5 * Math.exp(-0.03 * elapsedMins) + 4.0;
            }

            double roundedTemp = Math.round(currentTemp * 10.0) / 10.0;
            int battery = Math.max(15, 98 - (elapsedMins / 3));

            SimulatorState currState = stateLiveData.getValue();
            boolean isOnline = (currState != null) && currState.isWiFiConnected;

            ReadingEntity reading = new ReadingEntity(
                0, canId, System.currentTimeMillis(), roundedTemp, battery, activeBatchId, isOnline, System.currentTimeMillis()
            );

            CanStatus status;
            if (roundedTemp < -40.0 || roundedTemp > 100.0) status = CanStatus.BREACH;
            else if (roundedTemp <= 4.0) status = CanStatus.SAFE;
            else if (roundedTemp <= 8.0) status = CanStatus.WARNING;
            else status = (elapsedMins > 45) ? CanStatus.BREACH : CanStatus.WARNING;

            if (isOnline) {
                SimulatorState updated = new SimulatorState(
                    canId, true, true, roundedTemp, battery, elapsedMins, 0, profile,
                    "Transmitting live telemetry: " + roundedTemp + "°C (Status: " + status + ")"
                );
                stateLiveData.postValue(updated);

                if (listener != null) {
                    List<ReadingEntity> singleList = new ArrayList<>();
                    singleList.add(reading);
                    listener.onIngest(singleList, activeBatchId, status);
                }
            } else {
                offlineFlashBuffer.add(reading);
                SimulatorState updated = new SimulatorState(
                    canId, true, false, roundedTemp, battery, elapsedMins, offlineFlashBuffer.size(), profile,
                    "WiFi Disconnected. Buffered in ESP32 SPIFFS flash (" + offlineFlashBuffer.size() + " readings)"
                );
                stateLiveData.postValue(updated);
            }

            if (elapsedMins >= 120) {
                stopSimulation();
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

    public void toggleWiFiConnection() {
        SimulatorState currState = stateLiveData.getValue();
        if (currState == null) return;

        boolean newWiFiState = !currState.isWiFiConnected;
        if (newWiFiState) {
            List<ReadingEntity> bufferedToSync = new ArrayList<>(offlineFlashBuffer);
            offlineFlashBuffer.clear();

            SimulatorState updated = new SimulatorState(
                currState.canId, currState.isRunning, true, currState.currentTempC,
                currState.batteryLevel, currState.elapsedMinutes, 0, currState.coolingProfile,
                "WiFi Reconnected! Performing burst sync of " + bufferedToSync.size() + " stored readings..."
            );
            stateLiveData.postValue(updated);

            if (!bufferedToSync.isEmpty() && listener != null) {
                double lastTemp = bufferedToSync.get(bufferedToSync.size() - 1).temperatureC;
                CanStatus status = (lastTemp <= 4.0) ? CanStatus.SAFE : (lastTemp <= 8.0 ? CanStatus.WARNING : CanStatus.BREACH);
                listener.onIngest(bufferedToSync, activeBatchId, status);
            }
        } else {
            SimulatorState updated = new SimulatorState(
                currState.canId, currState.isRunning, false, currState.currentTempC,
                currState.batteryLevel, currState.elapsedMinutes, offlineFlashBuffer.size(), currState.coolingProfile,
                "WiFi connection dropped. Telemetry switching to ESP32 SPIFFS ring buffer."
            );
            stateLiveData.postValue(updated);
        }
    }

    public void injectBreachScenario() {
        SimulatorState currState = stateLiveData.getValue();
        String canId = (currState != null) ? currState.canId : "CAN-ESP32-01";
        startChillingCycle(canId, CoolingProfile.SLOW_BREACH);
    }

    public void stopSimulation() {
        if (simulationTask != null) {
            simulationTask.cancel(true);
            simulationTask = null;
        }
        SimulatorState currState = stateLiveData.getValue();
        if (currState != null) {
            stateLiveData.postValue(new SimulatorState(
                currState.canId, false, currState.isWiFiConnected, currState.currentTempC,
                currState.batteryLevel, currState.elapsedMinutes, currState.bufferedCount,
                currState.coolingProfile, "Simulation stopped."
            ));
        }
    }
}
