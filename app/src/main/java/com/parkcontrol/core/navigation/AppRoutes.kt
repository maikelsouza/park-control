package com.parkcontrol.core.navigation

sealed class AppRoutes(
    val route: String
) {

    data object Home : AppRoutes("home")

    data object Parking : AppRoutes("parking")

    data object MonthlyCustomers : AppRoutes("monthly_customers")

    data object MonthlyCustomersActive : AppRoutes("monthly_customers/active")

    data object MonthlyCustomersInactive : AppRoutes("monthly_customers/inactive")

    data object MonthlyCustomerForm : AppRoutes("monthly_customers/form?customerId={customerId}") {
        const val customerIdArg = "customerId"

        fun createRoute(customerId: Int? = null): String {
            return if (customerId == null) {
                "monthly_customers/form"
            } else {
                "monthly_customers/form?customerId=$customerId"
            }
        }
    }

    data object Settings : AppRoutes("settings")
}