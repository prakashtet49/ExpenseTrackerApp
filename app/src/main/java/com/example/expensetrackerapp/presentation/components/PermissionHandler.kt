package com.example.expensetrackerapp.presentation.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat

class PermissionHandler(private val context: Context) {
    
    fun hasAudioPermission(): Boolean {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        Log.d("PermissionHandler", "🔍 Audio permission check: $hasPermission")
        return hasPermission
    }
}

@Composable
fun rememberAudioPermissionLauncher(
    onPermissionResult: (Boolean) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
) { isGranted ->
    onPermissionResult(isGranted)
}
