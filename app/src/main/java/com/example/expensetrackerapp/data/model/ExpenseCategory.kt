package com.example.expensetrackerapp.data.model

enum class ExpenseCategory(val displayName: String) {
    // Food & Dining
    FOOD_DINING("Food & Dining"),
    GROCERIES("Groceries"),
    RESTAURANTS("Restaurants"),
    COFFEE_TEA("Coffee & Tea"),
    FAST_FOOD("Fast Food"),
    DELIVERY("Food Delivery"),
    
    // Transportation
    TRANSPORTATION("Transportation"),
    FUEL("Fuel & Gas"),
    PUBLIC_TRANSPORT("Public Transport"),
    TAXI_RIDESHARE("Taxi & Rideshare"),
    PARKING("Parking"),
    VEHICLE_MAINTENANCE("Vehicle Maintenance"),
    
    // Shopping & Retail
    SHOPPING("Shopping"),
    CLOTHING("Clothing & Apparel"),
    ELECTRONICS("Electronics"),
    BOOKS("Books & Media"),
    HOME_GOODS("Home & Garden"),
    PERSONAL_CARE("Personal Care"),
    
    // Entertainment & Leisure
    ENTERTAINMENT("Entertainment"),
    MOVIES_TV("Movies & TV"),
    GAMING("Gaming"),
    SPORTS("Sports & Fitness"),
    HOBBIES("Hobbies"),
    EVENTS("Events & Shows"),
    
    // Health & Wellness
    HEALTHCARE("Healthcare"),
    MEDICINE("Medicine & Pharmacy"),
    DOCTOR_VISITS("Doctor Visits"),
    DENTAL("Dental Care"),
    VISION("Vision Care"),
    FITNESS("Fitness & Gym"),
    
    // Housing & Utilities
    HOUSING("Housing"),
    RENT("Rent"),
    MORTGAGE("Mortgage"),
    UTILITIES("Utilities"),
    INTERNET("Internet & Phone"),
    MAINTENANCE("Home Maintenance"),
    
    // Education & Learning
    EDUCATION("Education"),
    TUITION("Tuition & Fees"),
    BOOKS_SUPPLIES("Books & Supplies"),
    COURSES("Online Courses"),
    WORKSHOPS("Workshops & Training"),
    
    // Business & Work
    BUSINESS("Business"),
    OFFICE_SUPPLIES("Office Supplies"),
    SOFTWARE("Software & Tools"),
    MARKETING("Marketing & Advertising"),
    PROFESSIONAL("Professional Services"),
    
    // Travel & Vacation
    TRAVEL("Travel"),
    ACCOMMODATION("Accommodation"),
    FLIGHTS("Flights"),
    CAR_RENTAL("Car Rental"),
    ACTIVITIES("Travel Activities"),
    
    // Personal & Miscellaneous
    PERSONAL("Personal"),
    GIFTS("Gifts"),
    DONATIONS("Donations & Charity"),
    INSURANCE("Insurance"),
    SUBSCRIPTIONS("Subscriptions"),
    OTHER("Other")
}
