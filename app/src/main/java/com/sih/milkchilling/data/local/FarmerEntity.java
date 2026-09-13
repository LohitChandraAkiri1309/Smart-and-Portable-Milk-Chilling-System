package com.sih.milkchilling.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "farmers")
public class FarmerEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String phone;
    public String village;
    public String collectionCenterId;
    public String activeCanId;

    public FarmerEntity(@NonNull String id, String name, String phone, String village, String collectionCenterId, String activeCanId) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.village = village;
        this.collectionCenterId = collectionCenterId;
        this.activeCanId = activeCanId;
    }
}
