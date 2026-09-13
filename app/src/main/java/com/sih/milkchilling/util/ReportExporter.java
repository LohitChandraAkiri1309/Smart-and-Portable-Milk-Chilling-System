package com.sih.milkchilling.util;

import android.content.Context;
import com.sih.milkchilling.data.local.ReadingEntity;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportExporter {

    public static File exportBatchToCsv(Context context, String batchId, List<ReadingEntity> readings) {
        try {
            String fileName = "ColdChain_Report_" + batchId + "_" + System.currentTimeMillis() + ".csv";
            File exportDir = new File(context.getExternalFilesDir(null), "Reports");
            if (!exportDir.exists()) exportDir.mkdirs();

            File file = new File(exportDir, fileName);
            FileWriter writer = new FileWriter(file);

            writer.append("Batch ID,Can ID,Timestamp,Formatted Time,Temperature (°C),Battery (%),Synced Status\n");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            for (ReadingEntity r : readings) {
                String formattedTime = dateFormat.format(new Date(r.timestamp));
                String syncedStr = r.isSynced ? "Synced (WiFi/REST)" : "Buffered (SPIFFS Flash)";
                writer.append(batchId).append(",")
                      .append(r.canId).append(",")
                      .append(String.valueOf(r.timestamp)).append(",")
                      .append(formattedTime).append(",")
                      .append(String.valueOf(r.temperatureC)).append(",")
                      .append(String.valueOf(r.batteryLevel)).append("%,")
                      .append(syncedStr).append("\n");
            }

            writer.flush();
            writer.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
