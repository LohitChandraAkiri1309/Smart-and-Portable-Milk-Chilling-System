package com.sih.milkchilling.data.local;

public enum CanStatus {
    SAFE,       // Below target threshold (< 4°C)
    WARNING,    // Between 4°C and 8°C
    BREACH,     // Above 8°C for extended time
    STALE       // Offline/No reading received
}
