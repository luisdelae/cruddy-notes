package com.luisd.cruddynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.luisd.cruddynotes.ui.NavigationHost
import com.luisd.cruddynotes.ui.theme.CruddyNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CruddyNotesTheme {
                NavigationHost(Modifier)
            }
        }
    }
}