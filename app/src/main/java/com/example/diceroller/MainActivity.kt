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
import androidx.compose.material3.OutlinedTextField
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

@Preview(showBackground = true)
@Composable
fun DiceRollerApp() {
    var gameState by remember { mutableStateOf(GameState()) }

    if (gameState.nameInputPhase) {
        NameInputScreen { p1Name, p2Name ->
            gameState = gameState.copy(
                player1Name = p1Name,
                player2Name = p2Name,
                nameInputPhase = false
            )
        }
    } else {
        GameScreen(
            gameState = gameState,
            onRollDice = { gameState = gameState.rollDice() },
            onPassTurn = { gameState = gameState.passTurn() },
            onRestart = { gameState = GameState() }
        )
    }
}

@Composable
fun GameScreen(
    gameState: GameState,
    onRollDice: () -> Unit,
    onPassTurn: () -> Unit,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostra de quem é a vez
        Text(
            text = if (gameState.currentPlayer == 1) "Vez do ${gameState.player1Name}"
            else "Vez do ${gameState.player2Name}",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mostra os pontos de cada jogador
        PlayerScore(gameState.player1Name, gameState.player1Score)
        Spacer(modifier = Modifier.height(8.dp))
        PlayerScore(gameState.player2Name, gameState.player2Score)

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
            onClick = onRollDice,
            enabled = !gameState.gameFinished
        ) {
            Text(text = "Rolar Dado", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botão de passar
        Button(
            onClick = onPassTurn,
            enabled = !gameState.gameFinished
        ) {
            Text(text = "Passar", fontSize = 24.sp)
        }

        // Mostra o resultado do jogo
        if (gameState.gameFinished) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = determineWinner(
                    gameState.player1Name,
                    gameState.player1Score,
                    gameState.player2Name,
                    gameState.player2Score
                ),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRestart
            ) {
                Text(text = "Jogar Novamente", fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun PlayerScore(playerName: String, score: Int) {
    Text(
        text = "$playerName: $score pontos",
        fontSize = 20.sp,
        color = when {
            score > 21 -> Color.Red
            playerName == "Player 1" -> Color(0xFF6200EE)
            else -> Color(0xFF03DAC6)
        },
        fontWeight = if (score > 21) FontWeight.Bold else FontWeight.Normal
    )
}

@Composable
fun NameInputScreen(
    onNamesEntered: (String, String) -> Unit
) {
    var player1Name by remember { mutableStateOf("") }
    var player2Name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Digite os nomes dos jogadores", fontSize = 24.sp, modifier = Modifier.padding(bottom = 24.dp))

        OutlinedTextField(
            value = player1Name,
            onValueChange = { player1Name = it },
            label = { Text("Nome do Player 1") },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = player2Name,
            onValueChange = { player2Name = it },
            label = { Text("Nome do Player 2") },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                val p1 = if (player1Name.isBlank()) "Player 1" else player1Name
                val p2 = if (player2Name.isBlank()) "Player 2" else player2Name
                onNamesEntered(p1, p2)
            },
            enabled = player1Name.isNotBlank() || player2Name.isNotBlank()
        ) {
            Text("Começar Jogo", fontSize = 20.sp)
        }
    }
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

private fun determineWinner(
    player1Name: String, score1: Int,
    player2Name: String, score2: Int
): String {
    return when {
        score1 > 21 && score2 > 21 -> "Ambos estouraram 21!"
        score1 > 21 -> "${player1Name} estourou! ${player2Name} ganhou!"
        score2 > 21 -> "${player2Name} estourou! ${player1Name} ganhou!"
        score1 == score2 -> "Empate! Ambos com $score1 pontos"
        score1 > score2 -> "${player1Name} ganhou com $score1 contra $score2!"
        else -> "${player2Name} ganhou com $score2 contra $score1!"
    }
}

data class GameState(
    val player1Score: Int = 0,
    val player2Score: Int = 0,
    val currentPlayer: Int = 1,
    val currentDiceValue: Int = 1,
    val gameFinished: Boolean = false,
    val player1Name: String = "Player 1",
    val player2Name: String = "Player 2",
    val nameInputPhase: Boolean = true
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