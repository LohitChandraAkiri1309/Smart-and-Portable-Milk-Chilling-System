package com.sih.milkchilling.data.local;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "readings",
    indices = {
        @Index(value = {"canId", "timestamp"}, unique = true),
        @Index(value = {"batchId"})
    }
)
public class ReadingEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String canId;
    public long timestamp;
    public double temperatureC;
    public int batteryLevel;
    public String batchId;
    public boolean isSynced;
    public long syncedAt;

    public ReadingEntity(long id, String canId, long timestamp, double temperatureC, int batteryLevel,
                         String batchId, boolean isSynced, long syncedAt) {
        this.id = id;
        this.canId = canId;
        this.timestamp = timestamp;
        this.temperatureC = temperatureC;
        this.batteryLevel = batteryLevel;
        this.batchId = batchId;
        this.isSynced = isSynced;
        this.syncedAt = syncedAt;
    }
}
