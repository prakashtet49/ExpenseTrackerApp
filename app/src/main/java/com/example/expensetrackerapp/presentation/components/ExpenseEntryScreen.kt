package com.example.expensetrackerapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetrackerapp.data.model.ExpenseCategory
import com.example.expensetrackerapp.presentation.viewmodel.ExpenseEntryViewModel
import com.example.expensetrackerapp.presentation.state.UiEvent
import com.example.expensetrackerapp.presentation.components.SpeechToTextField
import com.example.expensetrackerapp.presentation.components.ReceiptImageSelector
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import android.widget.Toast
import kotlinx.coroutines.delay
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    viewModel: ExpenseEntryViewModel = hiltViewModel(),
    onNavigateToExpenseList: () -> Unit,
    selectedDate: LocalDate? = null
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    
    // Animation states
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }
    
    // Handle UI events
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is UiEvent.NavigateToExpenseList -> {
                    onNavigateToExpenseList()
                }
            }
        }
    }
    
    // Set selected date when it changes
    LaunchedEffect(selectedDate) {
        viewModel.setSelectedDate(selectedDate)
    }

    // Total spent today is loaded automatically in ViewModel init

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Add Expense",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Date Indicator (if not today)
                selectedDate?.let { date ->
                    if (date != LocalDate.now()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                                            Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Selected Date",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Adding expense for ${date.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                
                // Total Spent Today Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (selectedDate == LocalDate.now() || selectedDate == null) 
                                "Total Spent Today" 
                            else 
                                "Total Spent on ${selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "₹${state.totalSpentToday}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Title Field with Mic
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = { viewModel.onTitleChange(it) },
                        label = { Text("Title") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    SpeechToTextField(
                        onTextResult = { viewModel.onTitleChange(it) },
                        fieldName = "Title"
                    )
                }

                // Amount Field with Mic
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.amount,
                        onValueChange = { viewModel.onAmountChange(it) },
                        label = { Text("Amount (₹)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        prefix = { Text("₹") }
                    )
                    SpeechToTextField(
                        onTextResult = { viewModel.onAmountChange(it) },
                        fieldName = "Amount"
                    )
                }

                // Category Dropdown
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.category.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExpenseCategory.values().forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName) },
                                onClick = {
                                    viewModel.onCategoryChanged(category)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Notes Field with Mic
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.notes,
                        onValueChange = { viewModel.onNotesChanged(it) },
                        label = { Text("Notes (Optional)") },
                        modifier = Modifier.weight(1f),
                        maxLines = 3,
                        singleLine = false
                    )
                    SpeechToTextField(
                        onTextResult = { viewModel.onNotesChanged(it) },
                        fieldName = "Notes"
                    )
                }

                // Receipt Image Section
                ReceiptImageSelector(
                    currentImagePath = state.receiptImagePath,
                    onImageSelected = { imagePath ->
                        viewModel.setReceiptImage(imagePath)
                    }
                )

                // Submit Button
                AnimatedVisibility(
                    visible = !showSuccessAnimation,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Button(
                        onClick = {
                            if (state.title.isNotBlank() && state.amount.isNotBlank()) {
                                isSubmitting = true
                                coroutineScope.launch {
                                    viewModel.submitExpense()
                                    // Show success animation
                                    showSuccessAnimation = true
                                    delay(1500) // Show animation for 1.5 seconds
                                    showSuccessAnimation = false
                                    isSubmitting = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = state.title.isNotBlank() && 
                                 state.amount.isNotBlank() && 
                                 !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = if (isSubmitting) "Adding..." else "Add Expense",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Success Animation
                AnimatedVisibility(
                    visible = showSuccessAnimation,
                    enter = scaleIn(
                        animationSpec = tween(300, easing = EaseOutBack)
                    ) + fadeIn(),
                    exit = scaleOut(
                        animationSpec = tween(200, easing = EaseInBack)
                    ) + fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 8.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AnimatedVisibility(
                                visible = showSuccessAnimation,
                                enter = scaleIn(
                                    animationSpec = tween(500, delayMillis = 200, easing = EaseOutBack)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .scale(
                                            animateFloatAsState(
                                                targetValue = if (showSuccessAnimation) 1f else 0f,
                                                animationSpec = tween(500, delayMillis = 200)
                                            ).value
                                        ),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            AnimatedVisibility(
                                visible = showSuccessAnimation,
                                enter = fadeIn(
                                    animationSpec = tween(500, delayMillis = 400)
                                ) + slideInVertically(
                                    animationSpec = tween(500, delayMillis = 400)
                                ) { it / 2 }
                            ) {
                                Text(
                                    text = "Expense Added Successfully!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
