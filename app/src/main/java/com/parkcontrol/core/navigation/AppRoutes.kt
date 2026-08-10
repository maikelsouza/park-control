package com.parkcontrol.core.navigation

sealed class AppRoutes(
    val route: String
) {

    data object Home : AppRoutes("home")

    data object Parking : AppRoutes("parking")

    data object ParkedVehicles : AppRoutes("parked_vehicles")

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

    data object CustomerVehicles : AppRoutes("customer_vehicles/{customerId}") {
        const val customerIdArg = "customerId"
        fun createRoute(customerId: Int) = "customer_vehicles/$customerId"
    }

    data object CustomerVehicleForm :
        AppRoutes("customer_vehicle_form/{customerId}?vehicleId={vehicleId}") {
        const val customerIdArg = "customerId"
        const val vehicleIdArg = "vehicleId"
        fun createRoute(customerId: Int, vehicleId: Int? = null) =
            "customer_vehicle_form/$customerId${if (vehicleId != null) "?vehicleId=$vehicleId" else ""}"
    }

    data object Settings : AppRoutes("settings")

    data object Agreements : AppRoutes("agreements")

    data object About : AppRoutes("about")
}