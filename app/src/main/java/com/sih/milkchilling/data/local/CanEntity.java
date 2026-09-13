package com.sih.milkchilling.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cans")
public class CanEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String currentFarmerId;
    public String collectionCenterId;
    public double targetTempC;
    public double warningTempC;
    public double currentTempC;
    public int batteryLevel;
    public CanStatus status;
    public String activeBatchId;
    public long lastSeenTimestamp;
    public boolean isConnectedWiFi;
    public int offlineBufferedCount;

    public CanEntity(@NonNull String id, String name, String currentFarmerId, String collectionCenterId,
                     double targetTempC, double warningTempC, double currentTempC, int batteryLevel,
                     CanStatus status, String activeBatchId, long lastSeenTimestamp,
                     boolean isConnectedWiFi, int offlineBufferedCount) {
        this.id = id;
        this.name = name;
        this.currentFarmerId = currentFarmerId;
        this.collectionCenterId = collectionCenterId;
        this.targetTempC = targetTempC;
        this.warningTempC = warningTempC;
        this.currentTempC = currentTempC;
        this.batteryLevel = batteryLevel;
        this.status = status;
        this.activeBatchId = activeBatchId;
        this.lastSeenTimestamp = lastSeenTimestamp;
        this.isConnectedWiFi = isConnectedWiFi;
        this.offlineBufferedCount = offlineBufferedCount;
    }
}
