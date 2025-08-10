package com.example.expensetrackerapp.presentation.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun SpeechToTextField(
    onTextResult: (String) -> Unit,
    fieldName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hasPermission = remember { 
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == 
        android.content.pm.PackageManager.PERMISSION_GRANTED 
    }
    
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("SpeechToTextField", "Permission result for $fieldName: $isGranted")
    }
    
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showSpeechDialog by remember { mutableStateOf(false) }
    var speechService by remember { mutableStateOf<SimpleSpeechService?>(null) }
    
    // Create speech service
    LaunchedEffect(Unit) {
        speechService = SimpleSpeechService(context)
    }
    
    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            speechService?.destroy()
        }
    }
    
    // Observe speech results
    LaunchedEffect(speechService) {
        speechService?.speechText?.collect { text ->
            if (text.isNotEmpty()) {
                Log.d("SpeechToTextField", "Speech result for $fieldName: $text")
                onTextResult(text)
                showSpeechDialog = false
            }
        }
    }
    
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable {
                Log.d("SpeechToTextField", "🎤 Mic clicked for $fieldName")
                if (hasPermission) {
                    showSpeechDialog = true
                } else {
                    showPermissionDialog = true
                }
            }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🎤",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
    
    // Permission Dialog
    if (showPermissionDialog) {
        Dialog(
            onDismissRequest = { showPermissionDialog = false },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎤",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Microphone Permission Required",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "This app needs microphone access to convert your speech to text for $fieldName.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant)
                                .clickable { showPermissionDialog = false }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancel",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable {
                                    showPermissionDialog = false
                                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Grant Permission",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Speech Dialog
    if (showSpeechDialog) {
        SimpleSpeechDialog(
            fieldName = fieldName,
            speechService = speechService,
            onDismiss = { showSpeechDialog = false }
        )
    }
}

@Composable
private fun SimpleSpeechDialog(
    fieldName: String,
    speechService: SimpleSpeechService?,
    onDismiss: () -> Unit
) {
    var isListening by remember { mutableStateOf(false) }
    var speechText by remember { mutableStateOf("") }
    
    // Observe speech service state
    LaunchedEffect(speechService) {
        speechService?.isListening?.collect { listening ->
            isListening = listening
        }
    }
    
    LaunchedEffect(speechService) {
        speechService?.speechText?.collect { text ->
            speechText = text
        }
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Speak for $fieldName",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Microphone button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            if (isListening) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primaryContainer
                        )
                        .clickable {
                            if (isListening) {
                                speechService?.stopListening()
                            } else {
                                speechService?.startListening()
                            }
                        }
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isListening) "🔇" else "🎤",
                        style = MaterialTheme.typography.headlineLarge,
                        color = if (isListening) MaterialTheme.colorScheme.onPrimary
                               else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (isListening) "Listening..." else "Tap to start speaking",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                if (speechText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(
                            text = "Recognized: $speechText",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onDismiss() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Close",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

// Simple speech service without parsing
class SimpleSpeechService(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()
    
    private val _speechText = MutableStateFlow("")
    val speechText: StateFlow<String> = _speechText.asStateFlow()
    
    fun startListening() {
        if (_isListening.value) return
        
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                        _speechText.value = ""
                        Log.d("SimpleSpeechService", "Ready for speech")
                    }
                    
                    override fun onBeginningOfSpeech() {
                        Log.d("SimpleSpeechService", "Beginning of speech")
                    }
                    
                    override fun onRmsChanged(rmsdB: Float) {}
                    
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    
                    override fun onEndOfSpeech() {
                        _isListening.value = false
                        Log.d("SimpleSpeechService", "End of speech")
                    }
                    
                    override fun onError(error: Int) {
                        _isListening.value = false
                        val errorMessage = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                            SpeechRecognizer.ERROR_NETWORK -> "Network error"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                            SpeechRecognizer.ERROR_SERVER -> "Server error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client error"
                            else -> "Unknown error: $error"
                        }
                        Log.e("SimpleSpeechService", "Speech recognition error: $errorMessage")
                        _speechText.value = ""
                    }
                    
                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (matches != null && matches.isNotEmpty()) {
                            val recognizedText = matches[0]
                            Log.d("SimpleSpeechService", "Speech recognized: $recognizedText")
                            _speechText.value = recognizedText
                        }
                    }
                    
                    override fun onPartialResults(partialResults: Bundle?) {}
                    
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
            
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }
            
            speechRecognizer?.startListening(intent)
            
        } catch (e: Exception) {
            Log.e("SimpleSpeechService", "Error starting speech recognition", e)
            _isListening.value = false
        }
    }
    
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            _isListening.value = false
        } catch (e: Exception) {
            Log.e("SimpleSpeechService", "Error stopping speech recognition", e)
        }
    }
    
    fun destroy() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            Log.e("SimpleSpeechService", "Error destroying speech recognizer", e)
        }
    }
}
