package com.sih.milkchilling;

import android.app.Application;

import com.sih.milkchilling.data.local.AppDatabase;
import com.sih.milkchilling.data.remote.LocalHttpTelemetryServer;
import com.sih.milkchilling.data.repository.MilkChillingRepository;

public class MilkChillingApp extends Application {

    private static MilkChillingApp instance;
    private AppDatabase database;
    private MilkChillingRepository repository;
    private LocalHttpTelemetryServer localServer;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = AppDatabase.getDatabase(this);
        repository = new MilkChillingRepository(database.milkChillingDao());

        // Start embedded HTTP Receiver for real ESP8266 / ESP32 hardware node connection
        localServer = new LocalHttpTelemetryServer();
        localServer.startServer();
    }

    public static MilkChillingApp getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public MilkChillingRepository getRepository() {
        return repository;
    }
}
