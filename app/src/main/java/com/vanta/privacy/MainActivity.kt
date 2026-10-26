package com.vanta.privacy

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.vanta.privacy.monitor.DeviceStatus
import com.vanta.privacy.monitor.DeviceStatusMonitor
import java.security.MessageDigest

class MainActivity : ComponentActivity() {
    private lateinit var monitor: DeviceStatusMonitor
    private var selectedFileUri by mutableStateOf<Uri?>(null)
    private var fileReport by mutableStateOf("Choose a file to calculate its SHA-256 fingerprint locally.")
    private val filePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            selectedFileUri = uri
            fileReport = inspectSelectedFile(uri)
        }
    }
    private val bluetoothPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        monitor = DeviceStatusMonitor(this)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VantaApp(
                        getStatus = { monitor.readStatus() },
                        requestBluetoothPermission = { requestBluetoothPermission() },
                        onChooseFile = { filePicker.launch(arrayOf("*/*")) },
                        fileReport = fileReport
                    )
                }
            }
        }
    }

    private fun inspectSelectedFile(uri: Uri): String {
        return try {
            val name = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && index >= 0) cursor.getString(index) else null
            } ?: uri.lastPathSegment ?: "Selected file"
            val digest = MessageDigest.getInstance("SHA-256")
            var bytes = 0L
            contentResolver.openInputStream(uri)?.use { input ->
                val buffer = ByteArray(16 * 1024)
                while (true) {
                    val count = input.read(buffer)
                    if (count < 0) break
                    digest.update(buffer, 0, count)
                    bytes += count
                }
            } ?: return "Could not open the selected file. Please choose it again."
            val hash = digest.digest().joinToString("") { "%02x".format(it) }
            "FILE FINGERPRINT\nName: $name\nSize: $bytes bytes\nSHA-256:\n$hash\n\nThis computes a file fingerprint only. It does not prove the file is safe or scan it for malware. Processing stays on this device."
        } catch (e: Exception) {
            "File inspection failed: ${e.javaClass.simpleName}: ${e.message ?: "unknown error"}"
        }
    }

    private fun requestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            bluetoothPermissionRequest.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }
}

private enum class VantaTab { HOME, FILE, URL }

@Composable
private fun VantaApp(
    getStatus: () -> DeviceStatus,
    requestBluetoothPermission: () -> Unit,
    onChooseFile: () -> Unit,
    fileReport: String
) {
    var tab by remember { mutableStateOf(VantaTab.HOME) }
    var status by remember { mutableStateOf(getStatus()) }
    var url by remember { mutableStateOf("") }
    var urlReport by remember { mutableStateOf("Enter a URL to run a local structure and warning check.") }

    LaunchedEffect(Unit) {
        while (true) {
            status = getStatus()
            kotlinx.coroutines.delay(5000)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("VANTA", style = MaterialTheme.typography.headlineLarge)
            Text("PRIVACY LAB  /  ON-DEVICE TOOLS", style = MaterialTheme.typography.labelMedium)
            Text("Local-first privacy utilities. No claim of complete device protection.")
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { tab = VantaTab.HOME }, modifier = Modifier.weight(1f)) { Text("Overview") }
                OutlinedButton(onClick = { tab = VantaTab.FILE }, modifier = Modifier.weight(1f)) { Text("File hash") }
                OutlinedButton(onClick = { tab = VantaTab.URL }, modifier = Modifier.weight(1f)) { Text("URL check") }
            }
        }
        when (tab) {
            VantaTab.HOME -> {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("DEVICE STATUS", style = MaterialTheme.typography.titleMedium)
                            Text("Network: ${status.networkSummary}")
                            Text("Wi-Fi: ${status.wifiSummary}")
                            Text("Bluetooth: ${status.bluetoothSummary}")
                            Text("VPN firewall: NOT IMPLEMENTED")
                            Button(onClick = { status = getStatus() }, modifier = Modifier.fillMaxWidth()) { Text("Refresh status") }
                            OutlinedButton(onClick = requestBluetoothPermission, modifier = Modifier.fillMaxWidth()) { Text("Grant Bluetooth status permission") }
                        }
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("WHAT THIS BUILD REALLY DOES", style = MaterialTheme.typography.titleMedium)
                            Text("• Reads basic Android network transport status.")
                            Text("• Reads Bluetooth enabled/disabled state when permission allows.")
                            Text("• Calculates SHA-256 for a file you explicitly select.")
                            Text("• Checks URL structure and common warning signs locally.")
                            Text("It does not block traffic, identify all malware, or query an online threat-intelligence service.")
                        }
                    }
                }
            }
            VantaTab.FILE -> {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("LOCAL FILE FINGERPRINT", style = MaterialTheme.typography.titleMedium)
                            Text("Choose a file through Android's system picker. VANTA reads it to calculate a SHA-256 hash; it does not upload the file.")
                            Button(onClick = onChooseFile, modifier = Modifier.fillMaxWidth()) { Text("Choose file") }
                            SelectionContainer { Text(fileReport, style = MaterialTheme.typography.bodyMedium) }
                            Text("A hash helps verify whether two files are identical. It is not a malware verdict.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            VantaTab.URL -> {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("LOCAL URL CHECK", style = MaterialTheme.typography.titleMedium)
                            OutlinedTextField(
                                value = url,
                                onValueChange = { url = it; urlReport = "Press Check URL to analyze this address." },
                                label = { Text("https://example.com") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Button(onClick = { urlReport = UrlAnalyzer.analyze(url) }, modifier = Modifier.fillMaxWidth()) { Text("Check URL") }
                            SelectionContainer { Text(urlReport) }
                            Text("This is a local heuristic check, not a reputation lookup. Do not enter private tokens or password-reset links.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            Text("VANTA Privacy · prototype build · features are labelled honestly", style = MaterialTheme.typography.labelSmall)
        }
    }
}

