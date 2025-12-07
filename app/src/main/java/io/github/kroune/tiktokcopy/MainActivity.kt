package io.github.kroune.tiktokcopy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.arkivanov.decompose.retainedComponent
import io.github.kroune.tiktokcopy.component.RootComponent
import io.github.kroune.tiktokcopy.data.preferences.FirstLaunchManager
import io.github.kroune.tiktokcopy.data.repository.ExpenseRepository
import io.github.kroune.tiktokcopy.ui.RootContent
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val repository: ExpenseRepository by inject()
    private val firstLaunchManager: FirstLaunchManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootComponent = retainedComponent {
            RootComponent(
                componentContext = it,
                repository = repository,
                firstLaunchManager = firstLaunchManager
            )
        }
        setContent {
            MaterialTheme {
                Surface {
                    RootContent(component = rootComponent)
                }
            }
        }
    }
}