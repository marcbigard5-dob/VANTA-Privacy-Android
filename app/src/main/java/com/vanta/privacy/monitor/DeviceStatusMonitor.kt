package com.vanta.privacy.monitor

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.content.ContextCompat

class DeviceStatusMonitor(private val context: Context) {
    fun readStatus(): DeviceStatus {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val caps = network?.let { cm.getNetworkCapabilities(it) }

        val networkText = when {
            caps == null -> "No active network detected"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Connected via Wi-Fi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Connected via mobile data"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Connected via Ethernet"
            else -> "Connected via another transport"
        }

        val wifiText = when {
            caps == null -> "No active network"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi is the active transport"
            else -> "Wi-Fi is not the active transport"
        }

        val bluetoothText = readBluetoothStatus()
        return DeviceStatus(networkText, wifiText, bluetoothText)
    }

    private fun readBluetoothStatus(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return "Permission not granted; Bluetooth state not read"
        }

        return try {
            val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val adapter = manager.adapter
            when {
                adapter == null -> "Bluetooth not supported on this device"
                adapter.isEnabled -> "Bluetooth is enabled"
                else -> "Bluetooth is disabled"
            }
        } catch (_: SecurityException) {
            "Permission denied; Bluetooth state unavailable"
        } catch (_: Exception) {
            "Bluetooth state unavailable"
        }
    }
}
