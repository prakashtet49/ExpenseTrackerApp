package com.example.expensetrackerapp.presentation.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReceiptImageSelector(
    currentImagePath: String?,
    onImageSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showImageOptions by remember { mutableStateOf(false) }
    
    // Camera launcher
    val cameraImageFile = remember { mutableStateOf<File?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageFile.value != null) {
            // Image was captured successfully
            onImageSelected(cameraImageFile.value!!.absolutePath)
        }
    }
    
    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            // Copy the selected image to app's private directory
            val copiedFile = copyImageToPrivateStorage(context, selectedUri)
            copiedFile?.let { file ->
                onImageSelected(file.absolutePath)
            }
        }
    }
    
    // Permission launcher for camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera(context, cameraLauncher, cameraImageFile)
        }
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        Text(
            text = "Receipt Image",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        // Image display or add button
        if (currentImagePath != null) {
            // Show current image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Image(
                    painter = rememberAsyncImagePainter(currentImagePath),
                    contentDescription = "Receipt Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Delete button overlay
                IconButton(
                    onClick = { onImageSelected(null) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Image",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        } else {
            // Show add image button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clickable { showImageOptions = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Receipt Image",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add Receipt Image",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Image options dialog
        if (showImageOptions) {
            AlertDialog(
                onDismissRequest = { showImageOptions = false },
                title = {
                    Text(
                        text = "Select Image Source",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Camera option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showImageOptions = false
                                                                         checkCameraPermissionAndLaunch(context, cameraPermissionLauncher, cameraLauncher, cameraImageFile)
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Take Photo",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        
                        // Gallery option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showImageOptions = false
                                    galleryLauncher.launch("image/*")
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Choose from Gallery",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showImageOptions = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private fun checkCameraPermissionAndLaunch(
    context: Context,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    cameraLauncher: androidx.activity.result.ActivityResultLauncher<Uri>,
    cameraImageFile: MutableState<File?>
) {
    when {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED -> {
            // Permission already granted, launch camera
            launchCamera(context, cameraLauncher, cameraImageFile)
        }
        else -> {
            // Request permission
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

private fun launchCamera(
    context: Context,
    cameraLauncher: androidx.activity.result.ActivityResultLauncher<Uri>,
    cameraImageFile: MutableState<File?>
) {
    try {
        // Create a temporary file for the image
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "RECEIPT_${timeStamp}_"
        val imageFile = File.createTempFile(imageFileName, ".jpg", context.cacheDir)
        
        // Store the file reference
        cameraImageFile.value = imageFile
        
        // Create content URI using FileProvider
        val imageUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        
        // Launch camera with the URI
        cameraLauncher.launch(imageUri)
        
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun copyImageToPrivateStorage(context: Context, sourceUri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(sourceUri)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "RECEIPT_${timeStamp}_"
        val imageFile = File.createTempFile(imageFileName, ".jpg", context.cacheDir)
        
        inputStream?.use { input ->
            imageFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        
        imageFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
