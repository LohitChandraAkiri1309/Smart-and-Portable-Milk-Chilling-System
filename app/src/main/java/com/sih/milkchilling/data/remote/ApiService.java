package com.sih.milkchilling.data.remote;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/v1/ingest")
    Call<IngestResponseDto> ingestBatch(@Body IngestBatchPayloadDto payload);

    @POST("api/v1/ingest/single")
    Call<IngestResponseDto> ingestSingle(@Body TelemetryReadingDto reading);
}
