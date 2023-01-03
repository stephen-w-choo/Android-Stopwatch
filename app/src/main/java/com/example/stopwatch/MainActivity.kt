package com.example.stopwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stopwatch.ui.theme.StopwatchTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StopwatchTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainAppScreen()
                }
            }
        }
    }
}

@Composable
private fun Heading(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 24.sp,
    )
}

@Composable
private fun TimesList(
    times: List<List<Double>>,
) {
    val listState = rememberLazyListState()
    // Remember a CoroutineScope to be able to launch
    LaunchedEffect(times.size) {
        listState.animateScrollToItem(index = times.size)
    }
    LazyColumn(
        modifier = Modifier
            .height(100.dp),
        reverseLayout= true,
        state=listState
    ) {
        itemsIndexed(times) { index, time ->
            Text(
                text = "# ${index + 1}          ${String.format("%.2f", time[0])}          ${String.format("%.2f", time[1])}",
                color = Color.White
            )
            }
        }
}

@Composable
private fun TimeDisplay(
    time: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .border(
                width = 8.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(50)
            )
            .clip(CircleShape)
            .size(256.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time.toString(),
            fontSize = 48.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
private fun StopwatchButton(buttonFunction: () -> Unit, imageResource: Int, modifier: Modifier, color: Color) {
    Button(
        modifier = modifier,
        onClick = buttonFunction,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color
        ),
    ) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = "reset",
            modifier = Modifier
                .size(36.dp)
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun StopwatchView() {
    var stopwatchTime by remember { mutableStateOf(0.0)}
    var timerActive by remember { mutableStateOf(false) }
    var reset by remember { mutableStateOf( true )}
    var startPauseButtonImage by remember { mutableStateOf( R.drawable.start )}
    var startPauseButtonShape by remember { mutableStateOf(
        Modifier
            .padding(8.dp)
            .clip(CircleShape)
            .size(100.dp)
    )}
    var times by remember { mutableStateOf (mutableListOf<List<Double>>()) }
    if (timerActive) {
        LaunchedEffect(Unit) {
            val timerStartTime = System.currentTimeMillis() - (stopwatchTime * 1000.0)
            while(true) {
                delay(10.milliseconds)
                stopwatchTime = ((System.currentTimeMillis() - timerStartTime) / 1000.0)
            }
        }
    }
    if (reset) {
        stopwatchTime = 0.0
        times.clear()
    }
    val startStopTimer = {
        if (!timerActive) {
            timerActive = true
            reset = false
            startPauseButtonImage = R.drawable.pause
            startPauseButtonShape = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(25))
                .size(100.dp)
        } else {
            timerActive = false
            startPauseButtonImage = R.drawable.start
            startPauseButtonShape = Modifier
                .padding(8.dp)
                .clip(CircleShape)
                .size(100.dp)
        }
    }
    val resetTimer = {
        timerActive = false;
        reset = true
        startPauseButtonImage = R.drawable.start;
    }
    val addTime: () -> Unit = {
        if (times.size == 0) {
            times.add(listOf(stopwatchTime, stopwatchTime))
        } else {
            times.add(listOf(stopwatchTime - times.last()[1], stopwatchTime))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Heading(text = "Stopwatch", modifier = Modifier)
        TimeDisplay(time = String.format("%.2f", stopwatchTime))
        TimesList(times = times)
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            StopwatchButton(
                buttonFunction = resetTimer,
                imageResource = R.drawable.reload,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .size(64.dp),
                color = Color.LightGray
            )
            StopwatchButton(
                buttonFunction =  startStopTimer,
                imageResource =  startPauseButtonImage,
                modifier = startPauseButtonShape,
                color = Color(
                    red = 20,
                    green = 100,
                    blue = 100
                )
            )
            if (timerActive) {
                StopwatchButton(
                    buttonFunction = addTime,
                    imageResource = R.drawable.ic_baseline_alarm_add_24,

                    modifier = Modifier
                        .padding(8.dp)
                        .clip(CircleShape)
                        .size(64.dp),
                    color = Color.LightGray
                )
            } else {
                Spacer(modifier = Modifier
                    .size(80.dp))
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    Surface(color = Color(red = 23, green = 23, blue = 23)) {
        StopwatchView()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StopwatchTheme {
        MainAppScreen()
    }
}