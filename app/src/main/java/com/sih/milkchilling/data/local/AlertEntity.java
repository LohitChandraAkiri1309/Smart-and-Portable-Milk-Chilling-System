package com.sih.milkchilling.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alerts")
public class AlertEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String canId;
    public String batchId;
    public AlertType type;
    public String message;
    public Double temperatureC;
    public long triggeredAt;
    public boolean isResolved;
    public Long resolvedAt;

    public AlertEntity(long id, String canId, String batchId, AlertType type, String message,
                       Double temperatureC, long triggeredAt, boolean isResolved, Long resolvedAt) {
        this.id = id;
        this.canId = canId;
        this.batchId = batchId;
        this.type = type;
        this.message = message;
        this.temperatureC = temperatureC;
        this.triggeredAt = triggeredAt;
        this.isResolved = isResolved;
        this.resolvedAt = resolvedAt;
    }
}
