package com.sih.milkchilling.data.remote;

import android.content.Context;

import com.google.gson.Gson;
import com.sih.milkchilling.MilkChillingApp;
import com.sih.milkchilling.data.local.ReadingEntity;
import com.sih.milkchilling.data.repository.MilkChillingRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class LocalHttpTelemetryServer {

    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private final int port = 8080;
    private final Gson gson = new Gson();

    public void startServer() {
        if (isRunning) return;
        isRunning = true;

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                serverSocket = new ServerSocket(port);
                while (isRunning && !serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    handleClient(socket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleClient(Socket socket) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                int contentLength = 0;

                // Read HTTP Request Headers
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    if (line.toLowerCase().startsWith("content-length:")) {
                        contentLength = Integer.parseInt(line.substring(15).trim());
                    }
                }

                // Read HTTP JSON Body
                char[] bodyChars = new char[contentLength];
                if (contentLength > 0) {
                    reader.read(bodyChars, 0, contentLength);
                }
                String jsonBody = new String(bodyChars);

                // Parse and ingest ESP8266 telemetry
                if (!jsonBody.isEmpty()) {
                    IngestBatchPayloadDto payload = gson.fromJson(jsonBody, IngestBatchPayloadDto.class);
                    if (payload != null && payload.readings != null && !payload.readings.isEmpty()) {
                        MilkChillingRepository repo = MilkChillingApp.getInstance().getRepository();
                        List<ReadingEntity> entities = new ArrayList<>();
                        for (TelemetryReadingDto dto : payload.readings) {
                            long ts = (dto.timestamp > 0) ? dto.timestamp : System.currentTimeMillis();
                            int bat = (dto.batteryLevel != null) ? dto.batteryLevel : 100;
                            entities.add(new ReadingEntity(0, payload.canId, ts, dto.temperatureC, bat, dto.batchId, true, System.currentTimeMillis()));
                        }
                        repo.ingestReadings(payload.canId, entities, payload.readings.get(0).batchId);
                    }
                }

                // Send HTTP 200 OK Response
                OutputStream out = socket.getOutputStream();
                String response = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nConnection: close\r\n\r\n{\"status\":\"SUCCESS\"}";
                out.write(response.getBytes());
                out.flush();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
