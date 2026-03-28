package com.keikuethas.irlhideandseek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.keikuethas.irlhideandseek.ui.theme.IRLHideAndSeekTheme
import com.keikuethas.irlhideandseek.view.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //MapKitFactory.setApiKey(R.string.MAPKIT_API_KEY.toString()) //TODO for map

        enableEdgeToEdge()
        setContent {
            GeneralScreen { innerPadding -> AppNavigation(innerPadding) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Эта функция нужна, чтобы задать общие настройки типа topBar и bottomBar
// Удобно для предпросмотра
fun GeneralScreen(screen: @Composable (PaddingValues) -> Unit) {
    IRLHideAndSeekTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { CenterAlignedTopAppBar(title = {Text("Hide and Seek")}) },
        ) { innerPadding ->
            screen(innerPadding)
        }
    }
}

@Preview
@Composable
fun GeneralScreenPreview() {
    GeneralScreen { }
}