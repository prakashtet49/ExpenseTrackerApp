# Expense Tracker App

A modern Android expense tracking application built with Jetpack Compose, following MVVM architecture and best practices for personal finance management.

## 🚀 Features

### Core Functionality
- **Expense Entry**: Add new expenses with title, amount, category, notes, and receipt images
- **Expense List**: View and manage expenses with date-based filtering
- **Category Management**: Predefined expense categories including "Other"
- **Receipt Images**: Capture photos or select from gallery for expense receipts
- **Date Selection**: Choose specific dates for expense entry and viewing

### Reporting & Analytics
- **Real-time Reports**: Generate reports for different time periods
- **Daily Breakdown**: View expenses aggregated by day
- **Category Analysis**: Track spending by category with percentages
- **Trend Analysis**: Monitor spending patterns (increasing, decreasing, stable)
- **Smart Alerts**: Top 2 most important expense alerts based on priority
- **Export Options**: Share reports as HTML, CSV, or plain text

### Time Periods
- Last 7 days
- Last 30 days  
- Last 3 months
- Current month
- Custom date ranges

### Expense Categories
- Food & Dining
- Transportation
- Shopping
- Entertainment
- Healthcare
- Utilities
- Education
- Travel
- Other

## 🏗️ Architecture

### Technology Stack
- **UI Framework**: Jetpack Compose
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Database**: Room Persistence Library
- **Dependency Injection**: Hilt
- **Asynchronous Programming**: Kotlin Coroutines & Flow
- **Image Loading**: Coil Compose
- **Material Design**: Material 3 with extended icons

### Project Structure
```
app/src/main/java/com/example/expensetrackerapp/
├── data/
│   ├── local/           # Room database, DAO, entities
│   ├── model/           # Data models and enums
│   └── repository/      # Repository interface and implementation
├── domain/
│   └── usecase/         # Business logic use cases
├── presentation/
│   ├── components/      # UI composables
│   ├── state/           # UI state classes
│   └── viewmodel/       # ViewModels for each screen
└── di/                  # Dependency injection modules
```

## 📱 Screens

### 1. Main Screen
- Navigation between different app sections
- Bottom navigation with expense entry, list, and reports

### 2. Expense Entry Screen
- **Form Fields**: Title, amount, category, notes, date
- **Receipt Image Selector**: Camera capture or gallery selection
- **Validation**: Ensures required fields are filled
- **Date Picker**: Select custom dates for expenses
- **Category Dropdown**: Choose from predefined categories

### 3. Expense List Screen
- **Date-based Filtering**: View expenses for specific dates
- **Grouping Options**: Group by category or show individual expenses
- **Total Summary**: Daily spending total and expense count
- **View Details**: Click "View" button on any expense to see complete details
- **Error Handling**: Retry mechanism for failed data loading

### 4. Report Screen
- **Date Range Selection**: Quick selection of common time periods
- **Summary Cards**: Total spending, expense count, and trends
- **Daily Breakdown**: Expenses aggregated by day
- **Category Analysis**: Spending distribution across categories
- **Smart Alerts**: Top 2 priority-based expense alerts
- **Export Options**: HTML, CSV, and text report formats

### 5. Expense Detail Screen
- **Complete Information**: View all expense details in one place
- **Receipt Display**: Shows attached receipt image if available
- **Formatted Details**: Date, time, category, notes, and amount
- **Navigation**: Easy back navigation to expense list

## 🔧 Implementation Details

### Database Schema
```sql
-- Expenses table
CREATE TABLE expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    amount REAL NOT NULL,
    category TEXT NOT NULL,
    notes TEXT,
    receiptImageUri TEXT,
    createdAt TEXT NOT NULL
);
```

### Key Components

#### ReceiptImageSelector
- **Camera Integration**: Uses `ActivityResultContracts.TakePicture`
- **Gallery Selection**: Uses `ActivityResultContracts.GetContent`
- **Permission Handling**: Camera and storage permissions
- **File Management**: Temporary file creation and private storage copying
- **UI Components**: Image preview, camera/gallery buttons, delete option

#### ExpenseDetailScreen
- **Complete Expense View**: Shows all expense information in a dedicated screen
- **Receipt Image Display**: Full-size receipt image viewing with proper scaling
- **Detailed Information**: Formatted display of date, time, category, notes, and amount
- **Navigation**: Integrated back navigation to return to expense list
- **Responsive Layout**: Scrollable content that works on all screen sizes

#### Report Generation
- **HTML Reports**: Structured HTML with CSS styling for better formatting
- **Data Aggregation**: Real-time calculation of daily and category totals
- **Trend Analysis**: Percentage change calculation for spending patterns
- **Alert System**: Priority-based alert ranking and filtering

#### Date Range Handling
- **SQL DATE() Function**: Ensures proper date comparison in database queries
- **LocalDate Integration**: Kotlin date handling with Room database
- **Time Zone Safety**: Consistent date handling across the application

### State Management
- **ExpenseEntryState**: Form data, validation, and UI state
- **ExpenseListState**: Expenses list, loading states, and errors
- **ReportState**: Report data, date ranges, and export states

## 📋 Permissions

### Required Permissions
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

### Features
```xml
<uses-feature android:name="android.hardware.camera" android:required="false" />
```

## 🎨 UI/UX Features

### Material Design 3
- **Consistent Top Bars**: Standardized across all screens
- **Color Scheme**: Dynamic color adaptation
- **Typography**: Material 3 typography scale
- **Elevation**: Proper card and surface elevation

### Responsive Design
- **Adaptive Layouts**: Works on different screen sizes
- **State Handling**: Loading, error, and empty states
- **Accessibility**: Content descriptions and semantic properties

### Visual Feedback
- **Loading Indicators**: Circular progress indicators
- **Error States**: Clear error messages with retry options
- **Success Feedback**: Toast messages for user actions
- **Visual Hierarchy**: Clear information organization

## 📊 Data Models

### Core Entities
```kotlin
data class Expense(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val notes: String? = null,
    val receiptImageUri: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class ExpenseCategory(val displayName: String) {
    FOOD("Food & Dining"),
    TRANSPORTATION("Transportation"),
    SHOPPING("Shopping"),
    ENTERTAINMENT("Entertainment"),
    HEALTHCARE("Healthcare"),
    UTILITIES("Utilities"),
    EDUCATION("Education"),
    TRAVEL("Travel"),
    OTHER("Other")
}
```

### Report Models
```kotlin
data class DailyTotal(
    val date: LocalDate,
    val amount: Double,
    val count: Int
)

data class CategoryTotal(
    val category: ExpenseCategory,
    val amount: Double,
    val count: Int,
    val percentage: Double
)
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (API level 24)
- Kotlin 1.9+
- Gradle 8.0+

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

### Build Commands
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Clean build
./gradlew clean assembleDebug
```

## 🔍 Key Features Explained

### Smart Alert System
The app implements a priority-based alert system that shows only the top 2 most important alerts:

1. **High Daily Spending**: Alerts when daily spending exceeds ₹1000
2. **High Category Spending**: Alerts when category spending exceeds ₹5000
3. **Rapid Spending Increase**: Alerts when spending trend shows >10% increase

**Priority Algorithm:**
- Daily/Category spending alerts ranked by amount (highest first)
- Rapid spending increase given medium priority
- Only top 2 alerts displayed to avoid UI clutter

### Receipt Image Management
- **Camera Integration**: Direct photo capture with temporary file handling
- **Gallery Selection**: Choose existing images from device
- **Storage Management**: Images copied to private app storage
- **File Provider**: Secure file sharing for camera integration

### Report Generation
- **HTML Format**: Rich formatting with CSS styling
- **Data Aggregation**: Real-time calculation of totals and percentages
- **Export Options**: Multiple formats for different use cases
- **Date Range Handling**: Inclusive date ranges with proper SQL queries

## 🐛 Troubleshooting

### Common Issues

#### Build Errors
- **Kotlin Version**: Ensure Kotlin 1.9+ compatibility
- **Dependencies**: Sync Gradle files after dependency changes
- **Clean Build**: Use `./gradlew clean` for dependency conflicts

#### Runtime Issues
- **Permissions**: Grant camera and storage permissions
- **File Provider**: Ensure proper file provider configuration
- **Database**: Check Room database version and migrations

#### UI Issues
- **Material Icons**: Extended icons library for additional icon support
- **Compose Version**: Ensure Compose version compatibility
- **Theme Issues**: Check Material 3 theme configuration

### Debug Information
The app includes comprehensive logging for debugging:
- Date range queries and results
- Database operation results
- Error handling and user feedback
- Performance metrics and data counts

## 🔮 Future Enhancements

### Planned Features
- **Budget Management**: Set and track spending limits
- **Recurring Expenses**: Automate regular expense entry
- **Data Backup**: Cloud sync and export options
- **Advanced Analytics**: Charts and spending insights
- **Multi-currency**: Support for different currencies
- **Export to PDF**: Direct PDF generation without HTML conversion

### Technical Improvements
- **Offline Support**: Better offline data handling
- **Performance**: Database query optimization
- **Testing**: Unit and UI tests
- **Accessibility**: Enhanced accessibility features

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review the implementation details above

---

## 📱 App Overview

The Expense Tracker App is a comprehensive personal finance management tool designed to help users track daily expenses, categorize spending, and gain insights into their financial habits. Built with modern Android development practices, it features an intuitive Material Design 3 interface, real-time expense analytics, smart spending alerts, and receipt image management. The app provides detailed reports, daily breakdowns, and category-wise spending analysis to help users make informed financial decisions.

## 🤖 AI Usage Summary

This project was developed with significant assistance from AI tools to accelerate development and ensure best practices:

- **ChatGPT (Claude/GPT-4)**: Used extensively for architectural decisions, code structure optimization, and debugging complex issues. Helped with Room database queries, Compose UI patterns, and Material Design 3 implementation.
- **GitHub Copilot**: Assisted with code completion, function signatures, and boilerplate code generation for Android components and Kotlin functions.
- **AI Code Review**: Leveraged AI tools for identifying potential bugs, suggesting performance improvements, and ensuring code follows Android development best practices.

## 📝 Prompt Logs

### Key Development Prompts Used:

**Architecture & Structure:**
```
"Help me design a clean MVVM architecture for an expense tracker app with Room database, Hilt dependency injection, and Jetpack Compose UI"
```

**Database Design:**
```
"Create Room database entities and DAOs for expense tracking with categories, dates, and receipt images"
```

**UI Components:**
```
"Design a Material Design 3 expense entry screen with category selection, amount input, and date picker"
```

**Feature Implementation:**
```
"Implement receipt image functionality with camera and gallery selection, including proper file management and permissions"
```

**Bug Fixing:**
```
"Fix date range query issue where end date expenses are excluded from reports"
```

**Code Optimization:**
```
"Optimize the expense list screen with proper state management and efficient data loading"
```

## ✅ Checklist of Features Implemented

### Core Features ✅
- [x] Expense entry with title, amount, category, notes, and date
- [x] Receipt image capture and gallery selection
- [x] Expense list with date-based filtering
- [x] Category management (9 predefined categories + "Other")
- [x] Date picker for expense entry and viewing
- [x] Expense detail view with complete information

### Reporting & Analytics ✅
- [x] Real-time expense reports
- [x] Daily breakdown by date
- [x] Category-wise spending analysis
- [x] Spending trend analysis (increasing/decreasing/stable)
- [x] Smart expense alerts (top 2 priority-based)
- [x] Export options (HTML, CSV, text)

### UI/UX Features ✅
- [x] Material Design 3 implementation
- [x] Consistent top bars across all screens
- [x] Responsive design for different screen sizes
- [x] Loading states and error handling
- [x] Visual feedback and accessibility

### Technical Features ✅
- [x] MVVM architecture with Jetpack Compose
- [x] Room database with proper schema
- [x] Hilt dependency injection
- [x] Kotlin Coroutines and Flow
- [x] Permission handling for camera and storage
- [x] File provider for secure image sharing

## 📥 APK Download

**Latest Release APK:**
- **Version**: 1.0.0
- **Size**: ~15 MB
- **Download Link**: [Expense Tracker App APK](https://github.com/yourusername/ExpenseTrackerApp/releases/latest)
- **Minimum Android**: API 24 (Android 7.0+)

**Build Instructions:**
```bash
# Generate debug APK
./gradlew assembleDebug

# Generate release APK
./gradlew assembleRelease
```

## 📸 Screenshots

### Main Screens
![Main Screen](screenshots/main_screen.png)
*Main navigation with bottom tabs for expense entry, list, and reports*

![Expense Entry](screenshots/expense_entry.png)
*Expense entry form with category selection and receipt image options*

![Expense List](screenshots/expense_list.png)
*Expense list with date filtering and view details option*

![Reports](screenshots/reports.png)
*Comprehensive reports with daily breakdown and category analysis*

![Expense Details](screenshots/expense_details.png)
*Detailed expense view with receipt image and complete information*

### Features Showcase
![Receipt Image](screenshots/receipt_image.png)
*Receipt image capture and gallery selection interface*

![Smart Alerts](screenshots/smart_alerts.png)
*Priority-based expense alerts system*

![Category Analysis](screenshots/category_analysis.png)
*Spending breakdown by category with percentages*

---

**Built with ❤️ using modern Android development practices**
