package com.example.diceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.diceroller.ui.theme.DiceRollerTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceRollerTheme {
                DiceRollerApp()
            }
        }
    }
}

@Preview
@Composable
fun DiceRollerApp() {
    var gameState by remember { mutableStateOf(GameState()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostra de quem é a vez
        Text(
            text = if (gameState.currentPlayer == 1) "Vez do Player 1" else "Vez do Player 2",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mostra os pontos de cada jogador
        PlayerScore(1, gameState.player1Score)
        Spacer(modifier = Modifier.height(8.dp))
        PlayerScore(2, gameState.player2Score)

        Spacer(modifier = Modifier.height(16.dp))

        // Mostra o dado atual
        Image(
            painter = painterResource(getDiceImage(gameState.currentDiceValue)),
            contentDescription = gameState.currentDiceValue.toString(),
            modifier = Modifier.height(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botão de rolar
        Button(
            onClick = { gameState = gameState.rollDice() },
            enabled = !gameState.gameFinished
        ) {
            Text(text = "Rolar Dado", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botão de passar
        Button(
            onClick = { gameState = gameState.passTurn() },
            enabled = !gameState.gameFinished
        ) {
            Text(text = "Passar", fontSize = 24.sp)
        }

        // Mostra o resultado do jogo
        if (gameState.gameFinished) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = determineWinner(gameState.player1Score, gameState.player2Score),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlayerScore(playerNumber: Int, score: Int) {
    Text(
        text = "Player $playerNumber: $score pontos",
        fontSize = 20.sp,
        color = if (score > 21) Color.Red else Color.Unspecified
    )
}

private fun getDiceImage(value: Int): Int {
    return when(value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }
}

private fun determineWinner(score1: Int, score2: Int): String {
    return when {
        score1 > 21 && score2 > 21 -> "Ambos perderam!"
        score1 > 21 -> "Player 2 ganhou!"
        score2 > 21 -> "Player 1 ganhou!"
        score1 == score2 -> "Empate!"
        score1 > score2 -> "Player 1 ganhou!"
        else -> "Player 2 ganhou!"
    }
}


@Composable
fun DiceWithButtonAndImage(modifier: Modifier = Modifier) {
    var result by remember { mutableStateOf( 1) }
    val imageResource = when(result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(imageResource), contentDescription = result.toString())

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { result = (1..6).random() },
        ) {
            Text(text = stringResource(R.string.roll), fontSize = 24.sp)
        }
    }
}

data class GameState(
    val player1Score: Int = 0,
    val player2Score: Int = 0,
    val currentPlayer: Int = 1, // 1 ou 2
    val currentDiceValue: Int = 1,
    val gameFinished: Boolean = false
) {
    fun rollDice(): GameState {
        if (gameFinished) return this

        val newValue = (1..6).random()
        val newScore = if (currentPlayer == 1) {
            player1Score + newValue
        } else {
            player2Score + newValue
        }

        return copy(
            player1Score = if (currentPlayer == 1) newScore else player1Score,
            player2Score = if (currentPlayer == 2) newScore else player2Score,
            currentDiceValue = newValue,
            gameFinished = newScore > 21
        )
    }

    fun passTurn(): GameState {
        if (gameFinished) return this

        return if (currentPlayer == 2) {
            copy(gameFinished = true)
        } else {
            copy(currentPlayer = 2, currentDiceValue = 1)
        }
    }
}