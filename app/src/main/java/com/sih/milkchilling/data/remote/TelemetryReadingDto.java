package com.sih.milkchilling.data.remote;

import com.google.gson.annotations.SerializedName;

public class TelemetryReadingDto {
    @SerializedName("can_id")
    public String canId;

    @SerializedName("timestamp")
    public long timestamp;

    @SerializedName("temperature_c")
    public double temperatureC;

    @SerializedName("battery_level")
    public Integer batteryLevel;

    @SerializedName("batch_id")
    public String batchId;

    public TelemetryReadingDto(String canId, long timestamp, double temperatureC, Integer batteryLevel, String batchId) {
        this.canId = canId;
        this.timestamp = timestamp;
        this.temperatureC = temperatureC;
        this.batteryLevel = batteryLevel;
        this.batchId = batchId;
    }
}
