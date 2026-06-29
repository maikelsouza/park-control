package com.parkcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.parkcontrol.core.navigation.AppNavigation
import com.parkcontrol.core.ui.theme.ParkControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParkControlTheme {
                AppNavigation()
            }
        }
    }


}