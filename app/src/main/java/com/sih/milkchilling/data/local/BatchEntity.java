package com.sih.milkchilling.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "batches")
public class BatchEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String canId;
    public String farmerId;
    public long startTime;
    public Long endTime;
    public double initialTempC;
    public double targetTempC;
    public double lowestTempRecordedC;
    public double maxTempRecordedC;
    public Integer timeToTargetMinutes;
    public boolean isCompliant;
    public String status;

    public BatchEntity(@NonNull String id, String canId, String farmerId, long startTime, Long endTime,
                       double initialTempC, double targetTempC, double lowestTempRecordedC,
                       double maxTempRecordedC, Integer timeToTargetMinutes, boolean isCompliant, String status) {
        this.id = id;
        this.canId = canId;
        this.farmerId = farmerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.initialTempC = initialTempC;
        this.targetTempC = targetTempC;
        this.lowestTempRecordedC = lowestTempRecordedC;
        this.maxTempRecordedC = maxTempRecordedC;
        this.timeToTargetMinutes = timeToTargetMinutes;
        this.isCompliant = isCompliant;
        this.status = status;
    }
}
