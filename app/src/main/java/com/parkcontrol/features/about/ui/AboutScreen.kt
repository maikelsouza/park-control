package com.parkcontrol.features.about.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.parkcontrol.BuildConfig
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
        val context = LocalContext.current
        val contactEmail = "maikel.souza@gmail.com"

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Gerencie seu estacionamento de forma rápida e simples, sem precisar pagar por um sistema para as atividades básicas. Controle entrada, saída, cadastro de veículos e clientes imediatamente, sem complicação.",
                    color = colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Versão ${BuildConfig.VERSION_NAME}",
                    color = colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp)
                )

                Text(
                    text = "Contato: $contactEmail",
                    color = colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$contactEmail")
                            }
                            context.startActivity(intent)
                        }
                )
            }
        }
    }
}



