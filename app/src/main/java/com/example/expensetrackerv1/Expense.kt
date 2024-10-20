package com.example.expensetrackerv1

data class Expense(
    val id: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: Long = 0,
    val description: String = "",
    val currency: String = "TRY" // Varsayılan olarak TRY tanımlandı
)


