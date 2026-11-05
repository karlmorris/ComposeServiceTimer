package edu.temple.composetimerservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.temple.composetimerservice.ui.theme.ComposeTimerServiceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTimerServiceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TimerComposable(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TimerComposable(modifier: Modifier = Modifier) {

    val timerVM : TimerVM = viewModel()
    var currentTimer by rememberSaveable{ mutableIntStateOf(0) }

    /*
        StateFlow.collect() is a suspendable function and must be called
        from another suspendable function or directly from a coroutine

        LaunchedEffect(key: Any, ...) launches a coroutine
        in a composable without the need for a callback
    */
    LaunchedEffect(true) {
        TimerInfo.currentTime.collect {
            currentTimer = it
        }
    }

    Column() {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(top = 22.dp, bottom = 22.dp),
            Arrangement.Top,
            Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Control Timer Below",
                color = Color.LightGray,
                modifier = modifier,
                fontSize = 22.sp
            )

            Row() {
                Spacer(Modifier.weight(1f))
                Button(
                    modifier = Modifier.weight(2f),
                    onClick = { timerVM.startTimer() }) { Text("START") }
                Spacer(Modifier.weight(1f))
                Button(
                    modifier = Modifier.weight(2f),
                    onClick = { timerVM.stopTimer() }) { Text("STOP") }
                Spacer(Modifier.weight(1f))
            }
        }

        Column(
            Modifier.fillMaxSize().background(brush = Brush.verticalGradient(listOf(Color.Blue, Color.Magenta, Color.Cyan))),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentTimer.toString(), // Read from global TimerInfo object
                //text = timerVM.currentTimer.toString(), // Read from ViewModel
                color = Color.White,
                fontSize = 240.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerComposablePreview() {
    ComposeTimerServiceTheme {
        TimerComposable()
    }
}