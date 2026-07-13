package com.parkcontrol.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.HowToReg
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalParking
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.PersonOff
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerScaffold(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()
    
    val isMonthlyCustomersExpanded = remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet {

                Text(
                    text = "🅿 ParkControl",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                NavigationDrawerItem(
                    label = {
                        Text("Home")
                    },
                    selected = currentRoute == AppRoutes.Home.route,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        onNavigate(AppRoutes.Home.route)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Home,
                            contentDescription = "Home"
                        )
                    }
                )

                NavigationDrawerItem(
                    label = {
                        Text("Estacionar")
                    },
                    selected = currentRoute == AppRoutes.Parking.route,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        onNavigate(AppRoutes.Parking.route)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.LocalParking,
                            contentDescription = "Estacionar"
                        )
                    }
                )

                NavigationDrawerItem(
                    label = {
                        Text("Listagem de veiculos")
                    },
                    selected = currentRoute == AppRoutes.ParkedVehicles.route,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        onNavigate(AppRoutes.ParkedVehicles.route)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.List,
                            contentDescription = "Listagem de veiculos"
                        )
                    }
                )

                NavigationDrawerItem(
                    label = {
                        Text("Clientes")
                    },
                    selected = currentRoute == AppRoutes.MonthlyCustomers.route || 
                               currentRoute == AppRoutes.MonthlyCustomersActive.route ||
                               currentRoute == AppRoutes.MonthlyCustomersInactive.route,
                    onClick = {
                        isMonthlyCustomersExpanded.value = !isMonthlyCustomersExpanded.value
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = "Clientes"
                        )
                    }
                )

                if (isMonthlyCustomersExpanded.value) {
                    NavigationDrawerItem(
                        label = {
                            Text("Ativos")
                        },
                        selected = currentRoute == AppRoutes.MonthlyCustomersActive.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            onNavigate(AppRoutes.MonthlyCustomersActive.route)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.HowToReg,
                                contentDescription = "Ativos"
                            )
                        },
                        modifier = Modifier.padding(start = 32.dp)
                    )

                    NavigationDrawerItem(
                        label = {
                            Text("Inativos")
                        },
                        selected = currentRoute == AppRoutes.MonthlyCustomersInactive.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            onNavigate(AppRoutes.MonthlyCustomersInactive.route)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.PersonOff,
                                contentDescription = "Inativos"
                            )
                        },
                        modifier = Modifier.padding(start = 32.dp)
                    )
                }

                NavigationDrawerItem(
                    label = {
                        Text("Configurações")
                    },
                    selected = currentRoute == AppRoutes.Settings.route,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        onNavigate(AppRoutes.Settings.route)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Configurações"
                        )
                    }
                )

                NavigationDrawerItem(
                    label = {
                        Text("Sobre")
                    },
                    selected = currentRoute == AppRoutes.About.route,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        onNavigate(AppRoutes.About.route)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = "Sobre"
                        )
                    }
                )
            }
        }
    ) {

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {

                TopAppBar(
                    title = {
                        Text(
                            text = "🅿️ ParkControl",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    navigationIcon = {

                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->

            content(paddingValues)
        }
    }
}