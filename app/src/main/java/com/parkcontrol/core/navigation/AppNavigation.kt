package com.parkcontrol.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import com.parkcontrol.features.agreements.ui.ActiveAgreementsScreen
import com.parkcontrol.features.agreements.ui.AgreementsScreen
import com.parkcontrol.features.agreements.ui.InactiveAgreementsScreen
import com.parkcontrol.features.parking.ui.ParkedVehiclesScreen
import com.parkcontrol.features.parking.ui.ParkingScreen
import com.parkcontrol.features.settings.ui.SettingsScreen

private const val MONTHLY_CUSTOMER_SAVE_RESULT_KEY = "monthly_customer_save_result"

@Composable
fun AppNavigation(

) {

    val navController = rememberNavController()

    // Navegacao usada pelos itens do drawer: evita empilhar multiplas
    // instancias da mesma tela (o que podia deixar estados antigos de
    // selecao/expansao do submenu "presos" ao voltar para uma tela ja
    // visitada) e restaura o estado salvo da tela de destino.
    val navigateFromDrawer: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Navegacao "para frente" (ex: abrir um formulario de "Novo"/"Editar" a
    // partir de uma tela de listagem, ou abrir a listagem de veiculos de um
    // cliente). Ao contrario de navigateFromDrawer, esta funcao apenas
    // empilha o destino normalmente, sem popUpTo/saveState/restoreState.
    // Usar navigateFromDrawer aqui era a causa do bug em que, apos visitar
    // uma tela via drawer a partir de um formulario, reabrir o mesmo
    // formulario pelo menu restaurava indevidamente a pilha antiga
    // (mostrando a tela de listagem de veiculos no lugar do formulario).
    val navigateForward: (String) -> Unit = { route ->
        navController.navigate(route)
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Home.route
    ) {

        composable(AppRoutes.Home.route) {
            HomeScreen(
                onNavigate = navigateFromDrawer
            )
        }

        composable(AppRoutes.Parking.route){
            ParkingScreen(
                onNavigate = navigateFromDrawer
            )
        }

        composable(AppRoutes.ParkedVehicles.route) {
            ParkedVehiclesScreen(
                onNavigate = navigateFromDrawer
            )
        }

        composable(AppRoutes.MonthlyCustomers.route) {
            val saveResult by it.savedStateHandle
                .getStateFlow(MONTHLY_CUSTOMER_SAVE_RESULT_KEY, null as String?)
                .collectAsState()

            ActiveMonthlyCustomersScreen(
                onNavigate = navigateFromDrawer,
                onNavigateForward = navigateForward,
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
                onNavigate = navigateFromDrawer,
                onNavigateForward = navigateForward,
                currentRoute = AppRoutes.MonthlyCustomersActive.route,
                saveSuccessMessage = saveResult,
                onSaveSuccessMessageShown = {
                    it.savedStateHandle[MONTHLY_CUSTOMER_SAVE_RESULT_KEY] = null
                }
            )
        }

        composable(AppRoutes.MonthlyCustomersInactive.route) {
            InactiveMonthlyCustomersScreen(
                onNavigate = navigateFromDrawer,
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
                onNavigate = navigateFromDrawer,
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
                onNavigate = navigateFromDrawer
            )

        }

        composable(AppRoutes.Agreements.route) {
            ActiveAgreementsScreen(
                onNavigate = navigateFromDrawer,
                onNavigateForward = navigateForward,
                currentRoute = AppRoutes.Agreements.route
            )
        }

        composable(AppRoutes.AgreementsActive.route) {
            ActiveAgreementsScreen(
                onNavigate = navigateFromDrawer,
                onNavigateForward = navigateForward,
                currentRoute = AppRoutes.AgreementsActive.route
            )
        }

        composable(AppRoutes.AgreementsInactive.route) {
            InactiveAgreementsScreen(
                onNavigate = navigateFromDrawer,
                currentRoute = AppRoutes.AgreementsInactive.route
            )
        }

        composable(
            route = AppRoutes.AgreementForm.route,
            arguments = listOf(
                navArgument(AppRoutes.AgreementForm.agreementIdArg) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val agreementId = backStackEntry
                .arguments
                ?.getInt(AppRoutes.AgreementForm.agreementIdArg)
                ?.takeIf { it > 0 }

            AgreementsScreen(
                onNavigate = navigateFromDrawer,
                agreementId = agreementId,
                onFinish = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.About.route) {
            AboutScreen(
                onNavigate = navigateFromDrawer
            )
        }
    }
}