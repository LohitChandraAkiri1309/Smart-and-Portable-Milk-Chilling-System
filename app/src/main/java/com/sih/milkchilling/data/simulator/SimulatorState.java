package com.sih.milkchilling.data.simulator;

public class SimulatorState {
    public String canId = "CAN-ESP32-01";
    public boolean isRunning = false;
    public boolean isWiFiConnected = true;
    public double currentTempC = 35.5;
    public int batteryLevel = 96;
    public int elapsedMinutes = 0;
    public int bufferedCount = 0;
    public CoolingProfile coolingProfile = CoolingProfile.NORMAL_PCM_SOLAR;
    public String statusText = "Ready to start demo chilling cycle";

    public SimulatorState() {}

    public SimulatorState(String canId, boolean isRunning, boolean isWiFiConnected, double currentTempC,
                          int batteryLevel, int elapsedMinutes, int bufferedCount,
                          CoolingProfile coolingProfile, String statusText) {
        this.canId = canId;
        this.isRunning = isRunning;
        this.isWiFiConnected = isWiFiConnected;
        this.currentTempC = currentTempC;
        this.batteryLevel = batteryLevel;
        this.elapsedMinutes = elapsedMinutes;
        this.bufferedCount = bufferedCount;
        this.coolingProfile = coolingProfile;
        this.statusText = statusText;
    }
}
