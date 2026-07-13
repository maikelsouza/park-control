package com.parkcontrol.features.about.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.parkcontrol.core.navigation.AppDrawerScaffold
import com.parkcontrol.core.navigation.AppRoutes

@Composable
fun AboutScreen(
    onNavigate: (String) -> Unit
) {
    AppDrawerScaffold(
        currentRoute = AppRoutes.About.route,
        onNavigate = onNavigate
    ) { paddingValues ->
        val colorScheme = MaterialTheme.colorScheme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Definir o texto que vai aqui",
                color = colorScheme.onBackground
            )
        }
    }
}


