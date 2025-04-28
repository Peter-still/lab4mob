package com.example.lessontictactoe

import android.os.CountDownTimer
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lessontictactoe.ui.theme.LessonTicTacToeTheme

@Composable
fun MainScreen(modifier: Modifier= Modifier) {
    var isDarkTheme by remember { mutableStateOf(false) }

    LessonTicTacToeTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { isDarkTheme = !isDarkTheme }) {
                    Text(if (isDarkTheme) "Light theme" else "Dark theme")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tic Tac Toe",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                GameBoard()

            }
        }
    }
}

@Composable
fun GameBoard()
{
    val dim=3
    val field= remember { mutableStateListOf(*Array(dim*dim) {"_"}) }
    var currentPlayer by remember { mutableStateOf("X") }
    var playerXScore by remember { mutableStateOf(0) }
    var playerOScore by remember { mutableStateOf(0) }
    var timerText by remember { mutableStateOf("10") }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }

    fun checkWinner(): String? {
        val winLines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        for (line in winLines) {
            val (a, b, c) = line
            if (field[a] != "_" && field[a] == field[b] && field[b] == field[c]) {
                return field[a]
            }
        }
        if (field.none { it == "_" }) return "D"
        return null
    }

    fun resetTimer(onFinish: () -> Unit) {
        timer?.cancel()
        timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                onFinish()
            }
        }.start()
    }

    fun nextTurn() {
        currentPlayer = if (currentPlayer == "X") "0" else "X"
        resetTimer { nextTurn() }
    }

    LaunchedEffect(Unit) {
        resetTimer { nextTurn() }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Player X: $playerXScore | Player 0: $playerOScore")
        Text("Time: $timerText s")
        Spacer(modifier = Modifier.height(16.dp))

        for (row in 0 until dim)
        {
            Row {
                for (col in 0 until dim) {
                    val index = row*dim+col
                    Box(
                        modifier= Modifier.size(40.dp)
                            .padding(4.dp)
                            .border(2.dp,
                                MaterialTheme.colorScheme.primary)
                            .clickable{
                                if (field[index] == "_" && checkWinner() == null) {
                                    field[index] = currentPlayer
                                    val winner = checkWinner()
                                    if (winner != null) {
                                        when (winner) {
                                            "X" -> playerXScore++
                                            "0" -> playerOScore++
                                        }
                                        timer?.cancel()
                                    } else {
                                        nextTurn()
                                    }
                                }
                            }
                        ,
                        contentAlignment = Alignment.Center
                    )
                    {
                        Text(
                            text=field[index],
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
        }

        Row(Modifier.padding(top = 16.dp)) {
            Button(onClick = {
                for (i in field.indices) field[i] = "_"
                currentPlayer = "X"
                resetTimer { nextTurn() }
            }) {
                Text("Reset round")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                for (i in field.indices) field[i] = "_"
                playerXScore = 0
                playerOScore = 0
                currentPlayer = "X"
                resetTimer { nextTurn() }
            }) {
                Text("New game")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview()
{
    LessonTicTacToeTheme {
        MainScreen()
    }
}