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
import com.parkcontrol.features.monthlyCustomers.ui.CustomerVehicleFormScreen
import com.parkcontrol.features.monthlyCustomers.ui.CustomerVehiclesScreen
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
                },
                onNewCustomerSaved = { newCustomerId ->
                    navController.navigate(AppRoutes.CustomerVehicles.createRoute(newCustomerId)) {
                        // Remove the form from the back stack so back goes to the list
                        popUpTo(AppRoutes.MonthlyCustomerForm.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Vehicles list ─────────────────────────────────────────────────
        composable(
            route = AppRoutes.CustomerVehicles.route,
            arguments = listOf(
                navArgument(AppRoutes.CustomerVehicles.customerIdArg) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val customerId = backStackEntry
                .arguments!!
                .getInt(AppRoutes.CustomerVehicles.customerIdArg)

            CustomerVehiclesScreen(
                customerId = customerId,
                onNavigate = { navController.navigate(it) },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Vehicle form ──────────────────────────────────────────────────
        composable(
            route = AppRoutes.CustomerVehicleForm.route,
            arguments = listOf(
                navArgument(AppRoutes.CustomerVehicleForm.customerIdArg) {
                    type = NavType.IntType
                },
                navArgument(AppRoutes.CustomerVehicleForm.vehicleIdArg) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val customerId = backStackEntry
                .arguments!!
                .getInt(AppRoutes.CustomerVehicleForm.customerIdArg)
            val vehicleId = backStackEntry
                .arguments!!
                .getInt(AppRoutes.CustomerVehicleForm.vehicleIdArg)
                .takeIf { it > 0 }

            CustomerVehicleFormScreen(
                customerId = customerId,
                vehicleId = vehicleId,
                onBack = { navController.popBackStack() },
                onFinish = { navController.popBackStack() }
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