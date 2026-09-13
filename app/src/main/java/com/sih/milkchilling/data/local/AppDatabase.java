package com.sih.milkchilling.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

@Database(
    entities = {
        CanEntity.class,
        ReadingEntity.class,
        BatchEntity.class,
        FarmerEntity.class,
        AlertEntity.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MilkChillingDao milkChillingDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "milk_chilling_db"
                    )
                    .addCallback(sRoomDatabaseCallback)
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                MilkChillingDao dao = INSTANCE.milkChillingDao();
                seedDemoData(dao);
            });
        }
    };

    private static void seedDemoData(MilkChillingDao dao) {
        List<FarmerEntity> farmers = Arrays.asList(
            new FarmerEntity("F-101", "Ramesh Kumar", "+91 98765 43210", "Anand Village", "CC-01", "CAN-ESP32-01"),
            new FarmerEntity("F-102", "Suresh Patel", "+91 98123 45678", "Kaira Village", "CC-01", "CAN-ESP32-02"),
            new FarmerEntity("F-103", "Anita Devi", "+91 99887 76655", "Vadodara North", "CC-01", "CAN-ESP32-03")
        );
        dao.insertFarmers(farmers);

        long now = System.currentTimeMillis();

        List<CanEntity> cans = Arrays.asList(
            new CanEntity("CAN-ESP32-01", "Smart Can #1 (Solar PCM)", "F-101", "CC-01", 4.0, 8.0, 3.8, 92, CanStatus.SAFE, "BATCH-20260901-01", now, true, 0),
            new CanEntity("CAN-ESP32-02", "Smart Can #2 (Transit)", "F-102", "CC-01", 4.0, 8.0, 6.4, 78, CanStatus.WARNING, "BATCH-20260901-02", now - 120000, false, 14),
            new CanEntity("CAN-ESP32-03", "Smart Can #3 (Breach Demo)", "F-103", "CC-01", 4.0, 8.0, 11.2, 45, CanStatus.BREACH, "BATCH-20260901-03", now - 60000, true, 0)
        );
        dao.insertCans(cans);

        BatchEntity batch1 = new BatchEntity(
            "BATCH-20260901-01", "CAN-ESP32-01", "F-101",
            now - (90 * 60 * 1000L), null, 35.5, 4.0, 3.8, 35.5, 68, true, "IN_PROGRESS"
        );
        dao.insertOrUpdateBatch(batch1);

        List<ReadingEntity> readingsBatch1 = new ArrayList<>();
        for (int i = 0; i <= 18; i++) {
            long tMs = now - (90 - i * 5) * 60 * 1000L;
            double temp = 35.5 * Math.exp(-0.025 * (i * 5)) + 3.2;
            double roundedTemp = Math.round(temp * 10.0) / 10.0;
            readingsBatch1.add(new ReadingEntity(0, "CAN-ESP32-01", tMs, roundedTemp, 95 - (i / 4), "BATCH-20260901-01", true, now));
        }
        dao.insertReadings(readingsBatch1);

        dao.insertAlert(new AlertEntity(0, "CAN-ESP32-03", "BATCH-20260901-03", AlertType.BREACH,
            "Cold-chain breach! Temperature reached 11.2°C (>8°C threshold)", 11.2, now - 300000L, false, null));
    }
}
