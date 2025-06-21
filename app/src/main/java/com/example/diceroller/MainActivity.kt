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
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
fun DiceRollerApp(){
    DiceWithButtonAndImage(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)
    )
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