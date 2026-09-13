package com.sih.milkchilling.data.repository;

import androidx.lifecycle.LiveData;

import com.sih.milkchilling.data.local.*;
import com.sih.milkchilling.data.simulator.ESP32HardwareSimulator;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MilkChillingRepository {

    private final MilkChillingDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public MilkChillingRepository(MilkChillingDao dao) {
        this.dao = dao;

        // Wire simulator listener to repository ingestion
        ESP32HardwareSimulator.getInstance().setOnReadingsIngestedListener((readings, batchId, status) -> {
            ingestReadings(readings.get(0).canId, readings, batchId);
        });
    }

    public LiveData<List<CanEntity>> getAllCansLiveData() {
        return dao.getAllCansLiveData();
    }

    public LiveData<CanEntity> getCanByIdLiveData(String canId) {
        return dao.getCanByIdLiveData(canId);
    }

    public LiveData<List<ReadingEntity>> getReadingsForCanLiveData(String canId) {
        return dao.getReadingsForCanLiveData(canId);
    }

    public LiveData<List<ReadingEntity>> getReadingsForBatchLiveData(String batchId) {
        return dao.getReadingsForBatchLiveData(batchId);
    }

    public LiveData<List<FarmerEntity>> getAllFarmersLiveData() {
        return dao.getAllFarmersLiveData();
    }

    public LiveData<List<AlertEntity>> getAllAlertsLiveData() {
        return dao.getAllAlertsLiveData();
    }

    public void ingestReadings(String canId, List<ReadingEntity> readings, String activeBatchId) {
        executor.execute(() -> {
            if (readings.isEmpty()) return;

            // 1. Insert readings (ignoring duplicates via unique index)
            dao.insertReadings(readings);

            ReadingEntity latestReading = readings.get(readings.size() - 1);
            double currentTemp = latestReading.temperatureC;

            // 2. Validate sensor range (FR4)
            if (currentTemp < -30.0 || currentTemp > 90.0) {
                dao.insertAlert(new AlertEntity(0, canId, activeBatchId, AlertType.SENSOR_FAULT,
                    "Invalid sensor reading detected: " + currentTemp + "°C", currentTemp, System.currentTimeMillis(), false, null));
                return;
            }

            // 3. Determine cold-chain status
            CanEntity can = dao.getCanById(canId);
            double targetTemp = (can != null) ? can.targetTempC : 4.0;
            double warningTemp = (can != null) ? can.warningTempC : 8.0;

            CanStatus newStatus;
            if (currentTemp <= targetTemp) newStatus = CanStatus.SAFE;
            else if (currentTemp <= warningTemp) newStatus = CanStatus.WARNING;
            else newStatus = CanStatus.BREACH;

            // 4. Update Can Entity
            if (can != null) {
                can.currentTempC = currentTemp;
                can.batteryLevel = latestReading.batteryLevel;
                can.status = newStatus;
                can.lastSeenTimestamp = latestReading.timestamp;
                if (activeBatchId != null) can.activeBatchId = activeBatchId;
                dao.insertOrUpdateCan(can);
            }

            // 5. Update or Create Batch Record & calculate "Time to Target"
            String batchId = (activeBatchId != null) ? activeBatchId : ((can != null) ? can.activeBatchId : "BATCH-" + canId);
            BatchEntity existingBatch = dao.getBatchById(batchId);

            if (existingBatch != null) {
                List<ReadingEntity> allBatchReadings = dao.getReadingsForBatch(batchId);
                double lowestTemp = currentTemp;
                double maxTemp = currentTemp;
                ReadingEntity firstTargetReading = null;

                for (ReadingEntity r : allBatchReadings) {
                    if (r.temperatureC < lowestTemp) lowestTemp = r.temperatureC;
                    if (r.temperatureC > maxTemp) maxTemp = r.temperatureC;
                    if (firstTargetReading == null && r.temperatureC <= targetTemp) {
                        firstTargetReading = r;
                    }
                }

                Integer timeToTargetMins = existingBatch.timeToTargetMinutes;
                if (firstTargetReading != null) {
                    timeToTargetMins = (int) ((firstTargetReading.timestamp - existingBatch.startTime) / (60 * 1000));
                }

                existingBatch.lowestTempRecordedC = Math.min(existingBatch.lowestTempRecordedC, lowestTemp);
                existingBatch.maxTempRecordedC = Math.max(existingBatch.maxTempRecordedC, maxTemp);
                existingBatch.timeToTargetMinutes = timeToTargetMins;
                existingBatch.isCompliant = (maxTemp <= warningTemp);
                dao.insertOrUpdateBatch(existingBatch);
            }

            // 6. Trigger Breach Alert if necessary
            if (newStatus == CanStatus.BREACH) {
                dao.insertAlert(new AlertEntity(0, canId, batchId, AlertType.BREACH,
                    "Cold-Chain Breach Alert: Can " + canId + " reached " + currentTemp + "°C (> " + warningTemp + "°C threshold)",
                    currentTemp, System.currentTimeMillis(), false, null));
            }
        });
    }

    public void addFarmer(FarmerEntity farmer) {
        executor.execute(() -> dao.insertFarmer(farmer));
    }

    public void addCan(CanEntity can) {
        executor.execute(() -> dao.insertOrUpdateCan(can));
    }

    public void resolveAlert(long alertId) {
        executor.execute(() -> dao.resolveAlert(alertId, System.currentTimeMillis()));
    }
}
