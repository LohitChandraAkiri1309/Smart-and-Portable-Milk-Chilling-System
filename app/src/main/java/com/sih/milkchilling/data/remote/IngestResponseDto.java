package com.sih.milkchilling.data.remote;

import com.google.gson.annotations.SerializedName;

public class IngestResponseDto {
    @SerializedName("success")
    public boolean success;

    @SerializedName("processed_count")
    public int processedCount;

    @SerializedName("ignored_duplicates")
    public int ignoredDuplicates;

    @SerializedName("message")
    public String message;
}
