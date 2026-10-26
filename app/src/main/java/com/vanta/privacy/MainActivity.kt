package com.vanta.privacy

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.vanta.privacy.monitor.DeviceStatus
import com.vanta.privacy.monitor.DeviceStatusMonitor

class MainActivity : ComponentActivity() {
    private lateinit var monitor: DeviceStatusMonitor

    private val bluetoothPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { refreshStatus() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        monitor = DeviceStatusMonitor(this)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VantaDashboard(
                        getStatus = { monitor.readStatus() },
                        requestBluetoothPermission = { requestBluetoothPermission() }
                    )
                }
            }
        }
    }

    private fun requestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            bluetoothPermissionRequest.launch(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            refreshStatus()
        }
    }

    private fun refreshStatus() {
        // The Compose screen refreshes on its own timer; this hook is kept for the permission result.
    }
}

@Composable
private fun VantaDashboard(
    getStatus: () -> DeviceStatus,
    requestBluetoothPermission: () -> Unit
) {
    var status by remember { mutableStateOf(getStatus()) }

    LaunchedEffect(Unit) {
        while (true) {
            status = getStatus()
            kotlinx.coroutines.delay(2500)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("VANTA Privacy", style = MaterialTheme.typography.headlineMedium)
            Text("Local monitoring prototype · v0.1.0")
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Privacy status", style = MaterialTheme.typography.titleMedium)
                    Text("Mode: monitor and alert")
                    Text("Processing: local by default")
                    Text("VPN firewall: NOT IMPLEMENTED")
                }
            }
        }
        item { StatusCard("Network", status.networkSummary, "Reports connectivity state only; does not inspect packet contents.") }
        item { StatusCard("Wi-Fi", status.wifiSummary, "Android may restrict detailed Wi-Fi information by OS version and permission.") }
        item { StatusCard("Bluetooth", status.bluetoothSummary, "Device details require permission and are intentionally not enumerated yet.") }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Permissions", style = MaterialTheme.typography.titleMedium)
                    Text("Bluetooth permission is requested only after you choose to check it.")
                    Button(onClick = requestBluetoothPermission) { Text("Check Bluetooth permission") }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Content Inspector", style = MaterialTheme.typography.titleMedium)
                    Text("Next milestone: user-selected file and URL inspection.")
                    Text("No files are uploaded or analyzed remotely by this prototype.")
                }
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
            OutlinedButton(onClick = { status = getStatus() }, modifier = Modifier.fillMaxWidth()) {
                Text("Refresh status")
            }
            Text(
                "Important: this prototype is a status dashboard, not a complete security product. It does not block traffic, inspect Bluetooth packets, or scan all apps.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun StatusCard(title: String, value: String, detail: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text("MONITOR")
            }
            Text(value)
            Text(detail, style = MaterialTheme.typography.bodySmall)
        }
    }
}
