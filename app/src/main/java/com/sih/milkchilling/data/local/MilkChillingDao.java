package com.sih.milkchilling.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface MilkChillingDao {
    // Cans
    @Query("SELECT * FROM cans")
    LiveData<List<CanEntity>> getAllCansLiveData();

    @Query("SELECT * FROM cans WHERE id = :canId")
    CanEntity getCanById(String canId);

    @Query("SELECT * FROM cans WHERE id = :canId")
    LiveData<CanEntity> getCanByIdLiveData(String canId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateCan(CanEntity can);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCans(List<CanEntity> cans);

    // Readings
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertReading(ReadingEntity reading);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertReadings(List<ReadingEntity> readings);

    @Query("SELECT * FROM readings WHERE canId = :canId ORDER BY timestamp DESC LIMIT 500")
    LiveData<List<ReadingEntity>> getReadingsForCanLiveData(String canId);

    @Query("SELECT * FROM readings WHERE batchId = :batchId ORDER BY timestamp ASC")
    LiveData<List<ReadingEntity>> getReadingsForBatchLiveData(String batchId);

    @Query("SELECT * FROM readings WHERE batchId = :batchId ORDER BY timestamp ASC")
    List<ReadingEntity> getReadingsForBatch(String batchId);

    // Batches
    @Query("SELECT * FROM batches ORDER BY startTime DESC")
    LiveData<List<BatchEntity>> getAllBatchesLiveData();

    @Query("SELECT * FROM batches WHERE id = :batchId")
    BatchEntity getBatchById(String batchId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateBatch(BatchEntity batch);

    // Farmers
    @Query("SELECT * FROM farmers")
    LiveData<List<FarmerEntity>> getAllFarmersLiveData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFarmer(FarmerEntity farmer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFarmers(List<FarmerEntity> farmers);

    // Alerts
    @Query("SELECT * FROM alerts ORDER BY triggeredAt DESC")
    LiveData<List<AlertEntity>> getAllAlertsLiveData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlert(AlertEntity alert);

    @Query("UPDATE alerts SET isResolved = 1, resolvedAt = :resolvedAt WHERE id = :alertId")
    void resolveAlert(long alertId, long resolvedAt);
}
