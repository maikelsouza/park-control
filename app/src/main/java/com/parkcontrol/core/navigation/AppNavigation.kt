package com.parkcontrol.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.parkcontrol.features.home.ui.HomeScreen
import com.parkcontrol.features.monthlyCustomers.ui.ActiveMonthlyCustomersScreen
import com.parkcontrol.features.monthlyCustomers.ui.InactiveMonthlyCustomersScreen
import com.parkcontrol.features.monthlyCustomers.ui.MonthlyCustomerFormScreen
import com.parkcontrol.features.parking.ui.ParkingScreen
import com.parkcontrol.features.settings.ui.SettingsScreen

@Composable
fun AppNavigation(

) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Home.route
    ) {

        composable(AppRoutes.Home.route) {
            HomeScreen(
                onNavigate = {
                    navController.navigate(it)
                }
            )
        }

        composable(AppRoutes.Parking.route){
            ParkingScreen(
                onNavigate = {
                    navController.navigate(it)
                }
            )
        }

        composable(AppRoutes.MonthlyCustomers.route) {
            ActiveMonthlyCustomersScreen(
                onNavigate = {
                    navController.navigate(it)
                },
                currentRoute = AppRoutes.MonthlyCustomers.route
            )
        }

        composable(AppRoutes.MonthlyCustomersActive.route) {
            ActiveMonthlyCustomersScreen(
                onNavigate = {
                    navController.navigate(it)
                },
                currentRoute = AppRoutes.MonthlyCustomersActive.route
            )
        }

        composable(AppRoutes.MonthlyCustomersInactive.route) {
            InactiveMonthlyCustomersScreen(
                onNavigate = {
                    navController.navigate(it)
                },
                currentRoute = AppRoutes.MonthlyCustomersInactive.route
            )
        }

        composable(
            route = AppRoutes.MonthlyCustomerForm.route,
            arguments = listOf(
                navArgument(AppRoutes.MonthlyCustomerForm.customerIdArg) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val customerId = backStackEntry
                .arguments
                ?.getInt(AppRoutes.MonthlyCustomerForm.customerIdArg)
                ?.takeIf { it > 0 }

            MonthlyCustomerFormScreen(
                onNavigate = { navController.navigate(it) },
                customerId = customerId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.Settings.route) {
            SettingsScreen(
                onNavigate = {
                    navController.navigate(it)
                }
            )

        }
    }
}