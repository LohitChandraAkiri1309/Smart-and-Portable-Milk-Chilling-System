package com.sih.milkchilling.data.remote;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class IngestBatchPayloadDto {
    @SerializedName("can_id")
    public String canId;

    @SerializedName("device_api_key")
    public String apiKey = "sih-26110-secret-key";

    @SerializedName("readings")
    public List<TelemetryReadingDto> readings;

    public IngestBatchPayloadDto(String canId, List<TelemetryReadingDto> readings) {
        this.canId = canId;
        this.readings = readings;
    }
}
