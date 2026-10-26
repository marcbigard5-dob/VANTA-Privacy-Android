package com.vanta.privacy.monitor

data class DeviceStatus(
    val networkSummary: String,
    val wifiSummary: String,
    val bluetoothSummary: String
)
