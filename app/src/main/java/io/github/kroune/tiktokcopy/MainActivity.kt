package io.github.kroune.tiktokcopy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.defaultComponentContext
import io.github.kroune.tiktokcopy.component.ExpenseScreenComponent
import io.github.kroune.tiktokcopy.ui.screens.ExpenseScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val component = ExpenseScreenComponent(defaultComponentContext())
                    val state by component.state.collectAsState()

                    ExpenseScreen(
                        state = state,
                        onEvent = component::onEvent
                    )
                }
            }
        }
    }
}