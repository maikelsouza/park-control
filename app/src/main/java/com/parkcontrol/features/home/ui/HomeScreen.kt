package com.parkcontrol.features.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkcontrol.core.navigation.AppDrawerScaffold

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit
) {

    AppDrawerScaffold(
        currentRoute = "home",
        onNavigate = onNavigate
    ) { padding ->

        Column(
            modifier = Modifier.padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "Bem-vindo ao ParkControl",
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Escolha uma opção no menu."
            )
        }
    }
}