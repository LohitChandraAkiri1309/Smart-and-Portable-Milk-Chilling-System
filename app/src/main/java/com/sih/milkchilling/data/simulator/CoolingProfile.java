package com.sih.milkchilling.data.simulator;

public enum CoolingProfile {
    NORMAL_PCM_SOLAR,  // Cools fast 35°C -> 4°C in 60-70 mins
    SLOW_BREACH,        // Fails to cool below 8°C (ambient heat breach)
    SENSOR_FAULT        // Out-of-range readings (-50°C / +150°C)
}
