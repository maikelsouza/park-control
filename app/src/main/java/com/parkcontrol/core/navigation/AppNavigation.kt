package com.parkcontrol.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.parkcontrol.features.home.ui.HomeScreen
import com.parkcontrol.features.monthlyCustomers.ui.ActiveMonthlyCustomersScreen
import com.parkcontrol.features.monthlyCustomers.ui.InactiveMonthlyCustomersScreen
import com.parkcontrol.features.monthlyCustomers.ui.MonthlyCustomerFormScreen
import com.parkcontrol.features.about.ui.AboutScreen
import com.parkcontrol.features.parking.ui.ParkedVehiclesScreen
import com.parkcontrol.features.parking.ui.ParkingScreen
import com.parkcontrol.features.settings.ui.SettingsScreen

private const val MONTHLY_CUSTOMER_SAVE_RESULT_KEY = "monthly_customer_save_result"

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

        composable(AppRoutes.ParkedVehicles.route) {
            ParkedVehiclesScreen(
                onNavigate = {
                    navController.navigate(it)
                }
            )
        }

        composable(AppRoutes.MonthlyCustomers.route) {
            val saveResult by it.savedStateHandle
                .getStateFlow(MONTHLY_CUSTOMER_SAVE_RESULT_KEY, null as String?)
                .collectAsState()

            ActiveMonthlyCustomersScreen(
                onNavigate = {
                    navController.navigate(it)
                },
                currentRoute = AppRoutes.MonthlyCustomers.route,
                saveSuccessMessage = saveResult,
                onSaveSuccessMessageShown = {
                    it.savedStateHandle[MONTHLY_CUSTOMER_SAVE_RESULT_KEY] = null
                }
            )
        }

        composable(AppRoutes.MonthlyCustomersActive.route) {
            val saveResult by it.savedStateHandle
                .getStateFlow(MONTHLY_CUSTOMER_SAVE_RESULT_KEY, null as String?)
                .collectAsState()

            ActiveMonthlyCustomersScreen(
                onNavigate = {
                    navController.navigate(it)
                },
                currentRoute = AppRoutes.MonthlyCustomersActive.route,
                saveSuccessMessage = saveResult,
                onSaveSuccessMessageShown = {
                    it.savedStateHandle[MONTHLY_CUSTOMER_SAVE_RESULT_KEY] = null
                }
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
                onBack = { navController.popBackStack() },
                onSaveSuccess = { message ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(MONTHLY_CUSTOMER_SAVE_RESULT_KEY, message)
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.Settings.route) {
            SettingsScreen(
                onNavigate = {
                    navController.navigate(it)
                }
            )

        }

        composable(AppRoutes.About.route) {
            AboutScreen(
                onNavigate = {
                    navController.navigate(it)
                }
            )
        }
    }
}