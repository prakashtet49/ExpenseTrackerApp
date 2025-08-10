package com.example.expensetrackerapp

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ExpenseTrackerApplication : Application() {
    
    companion object {
        private const val TAG = "ExpenseTrackerApp"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application onCreate: Initializing Expense Tracker App")
        
        // Initialize app-wide configurations here
        initializeApp()
    }
    
    private fun initializeApp() {
        try {
            // Initialize any app-wide configurations
            // For example: Crash reporting, analytics, etc.
            Log.d(TAG, "App initialization completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during app initialization", e)
        }
    }
    
    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "Application onTerminate: Cleaning up resources")
    }
}
